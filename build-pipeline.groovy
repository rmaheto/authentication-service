def runPipeline(Map config) {
    node(config.agent ?: 'Linux-RHEL7-Shared-BuildAgent') {
        def SERVICE_NAME = config.serviceName
        def GIT_BRANCH = config.branch
        def GIT_REPO = config.repo
        def BUILD_TYPE = config.buildType
        def VERSION
        def PROPS = config.props
        def FULL_DIR = config.dir // 🛠️ Full absolute path
        def S3_BUCKET = PROPS['S3_BUCKET']

        echo "🚀 Starting pipeline for service: ${SERVICE_NAME}"
        echo "🔗 Repo: ${GIT_REPO}"
        echo "🌿 Branch: ${GIT_BRANCH}"


        echo '🔧 Installing required tools...'
        sh 'python3 -m pip install pyyaml'


        stage('Validate Params') {
            echo "✅ BUILD_TYPE: ${BUILD_TYPE}"
        }

        stage('Build') {
            dir(FULL_DIR) {
                script {

                    echo '🧹 Cleaning environment...'
                    def cleanStatus = sh(script: 'mvn clean', returnStatus: true)
                    if (cleanStatus != 0) {
                        error "❌ Maven clean failed with exit code ${cleanStatus}"
                    }

                    if (!PROPS['SOLUTION_ID'] || !PROPS['APPLICATION']) {
                        error "❌ Missing required fields in input.json (SOLUTION_ID or APPLICATION)"
                    }

                    def pom = readMavenPom file: 'pom.xml'
                    VERSION = "${pom.version}.${env.BUILD_NUMBER}"
                    env.VERSION = VERSION

                    echo "🧪 Running tests..."
                    def testStatus = sh(script: 'mvn test', returnStatus: true)
                    if (testStatus != 0) {
                        error "❌ Tests failed with exit code ${testStatus}"
                    }

                    echo "🏗️  Building version: ${VERSION}"
                    def buildStatus = sh(script: 'mvn install', returnStatus: true)
                    if (buildStatus != 0) {
                        error "❌ Maven install failed with exit code ${buildStatus}"
                    }
                }
            }
        }


        if (['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString())) {
            stage('Publish') {
                dir(FULL_DIR) {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-jenkins-creds']]) {
                        sh """
                echo "📦 Renaming jar to match version ${VERSION}..."
                cp target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar target/${SERVICE_NAME}-${VERSION}.jar

                echo "📤 Uploading jar to S3... codemaniac-storage"
                aws s3 cp target/${SERVICE_NAME}-${VERSION}.jar s3://${S3_BUCKET}/${SERVICE_NAME}/${SERVICE_NAME}-${VERSION}.jar
            """
                    }
                }
            }
        }


        if (['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString())) {
            stage('Tag')
            dir(FULL_DIR) {
                script {
                    def SSH_ID = PROPS['GIT_CREDENTIALS']
                    echo "🏷️ Tagging repo with ${PROPS['APPLICATION']}_${VERSION}"
                    sshagent([SSH_ID]) {
                        sh """
                    git config user.name "jenkins"
                    git config user.email "jenkins@example.com"
                    git tag -a ${PROPS['APPLICATION']}_${VERSION} -m 'Tagged from ${GIT_REPO}'
                    git push origin --tags
                """
                    }
                }
            }
        }

        if (BUILD_TYPE.toString() == 'build_publish_deploy') {
            stage('Deploy')
            dir(FULL_DIR) {
                echo "🚀 Deploying application..."
                sshagent(credentials: ['kong-server-ssh-key']) {
                    sh """
                            scp -o StrictHostKeyChecking=no kong.yml ec2-user@44.222.204.57:/home/ec2-user/kong.yml
                            ssh -o StrictHostKeyChecking=no ec2-user@44.222.204.57 "docker exec kong kong reload"
                        """
                }
            }
        }


        script {
            def subject = currentBuild.currentResult == 'SUCCESS' ? "✅ Build Success" : "❌ Build Failed"
            def body = """
            <p>Build <b>${currentBuild.fullDisplayName}</b> finished with result: <b>${currentBuild.currentResult}</b></p>
            <p>Branch: ${GIT_BRANCH}</p>
            <p>Version: ${VERSION}</p>
            <p><a href="${env.BUILD_URL}">Click here to view the build</a></p>
        """

            emailext(
                    subject: subject,
                    body: body,
                    to: 'rmkaheto@gmail.com'
            )
        }

    }
}

return this
