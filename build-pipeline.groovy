def runPipeline(Map config) {
    node(config.agent ?: 'Linux-RHEL7-Shared-BuildAgent') {
        def SERVICE_NAME = config.serviceName
        def GIT_BRANCH = config.branch
        def GIT_REPO = config.repo
        def BUILD_TYPE = config.buildType
        def VERSION
        def PROPS = config.props
        def CHECKOUT_DIR = config.dir ?: SERVICE_NAME
        def FULL_DIR = config.dir // 🛠️ Full absolute path

        echo "🚀 Starting pipeline for service: ${SERVICE_NAME}"
        echo "🔗 Repo: ${GIT_REPO}"
        echo "🌿 Branch: ${GIT_BRANCH}"

        stage('Tool Install') {
            echo '🔧 Installing required tools...'
            sh 'python3 -m pip install pyyaml'
        }

        stage('Validate Params') {
            echo "✅ BUILD_TYPE: ${BUILD_TYPE}"
        }

        stage('Setup') {
            dir(FULL_DIR) {
                echo '🧹 Cleaning environment...'
                def cleanStatus = sh(script: 'mvn clean', returnStatus: true)
                if (cleanStatus != 0) {
                    error "❌ Maven clean failed with exit code ${cleanStatus}"
                }

                if (!PROPS['SOLUTION_ID'] || !PROPS['APPLICATION']) {
                    error "❌ Missing required fields in input.json (SOLUTION_ID or APPLICATION)"
                }
            }
        }

        stage('Build') {
            dir(FULL_DIR) {
                script {
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
                    echo "📤 Publishing artifact version ${VERSION}..."
                    sh "echo Simulating publish of artifact version ${VERSION}"
                }
            }
        }


        if (['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString())) {
            stage('Tag')
            dir(FULL_DIR) {
                script {
                    def SSH_ID = "${PROPS['SOLUTION_ID']}_ssh"
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


        stage('Post Actions') {
            echo "✅ Pipeline complete for ${SERVICE_NAME} (${BUILD_TYPE})"
        }
    }
}

return this
