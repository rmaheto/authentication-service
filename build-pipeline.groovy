def runPipeline(Map config) {
    node(config.agent ?: 'Linux-RHEL7-Shared-BuildAgent') {
        def SERVICE_NAME = config.serviceName
        echo "Starting pipeline for service: ${SERVICE_NAME}"
        def GIT_BRANCH = config.branch
        def GIT_REPO = config.repo
        def BUILD_TYPE = config.buildType
        def PROPS
        def VERSION

        stage('Checkout SCM') {
            echo "Checking out branch '${GIT_BRANCH}' from '${GIT_REPO}'"
            checkout([
                    $class           : 'GitSCM',
                    branches         : [[name: GIT_BRANCH]],
                    extensions       : [],
                    userRemoteConfigs: [[
                                                credentialsId: config.gitCredentialsId,
                                                url          : GIT_REPO
                                        ]]
            ])
        }

        stage('Tool Install') {
            echo 'Installing required tools...'
            sh 'python3 -m pip install pyyaml'
        }

        stage('Validate Params') {
            echo "BUILD_TYPE: ${BUILD_TYPE}"
            echo "GIT_BRANCH: ${GIT_BRANCH}"
        }

        stage('Setup') {
            echo 'Preparing environment...'
            sh 'mvn clean'
            PROPS = readJSON file: 'ci/input.json'

            if (!PROPS['SOLUTION_ID'] || !PROPS['APPLICATION']) {
                error "Missing required fields in input.json (SOLUTION_ID or APPLICATION)"
            }
        }

        stage('Build') {
            script {
                def pom = readMavenPom file: 'pom.xml'
                VERSION = "${pom.version}.${env.BUILD_NUMBER}"
                env.VERSION = VERSION

                echo "Running tests..."
                sh 'mvn test'

                echo "Building version: ${VERSION}"
                sh 'mvn install'
            }
        }

        stage('Publish') {
            steps {
                script {
                    if (['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString())) {
                        echo "Publishing artifact for version ${VERSION}..."
                        sh "echo Simulating publish of artifact version ${VERSION}"
                    } else {
                        echo "Skipping publish step (BUILD_TYPE = ${BUILD_TYPE})"
                    }
                }
            }
        }

        stage('Tag') {
            steps {
                script {
                    if (['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString())) {
                        def SSH_ID = "${PROPS['SOLUTION_ID']}_ssh"
                        echo "Tagging repo with ${PROPS['APPLICATION']}_${VERSION}"
                        sshagent([SSH_ID]) {
                            sh """
                                git config user.name "jenkins"
                                git config user.email "jenkins@example.com"
                                git tag -a ${PROPS['APPLICATION']}_${VERSION} -m 'Tagged by Jenkins'
                                git push origin --tags
                            """
                        }
                    } else {
                        echo "Skipping tagging step (BUILD_TYPE = ${BUILD_TYPE})"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if ('build_publish_deploy' == BUILD_TYPE.toString()) {
                        echo "Deploying application..."
                        sshagent(credentials: ['kong-server-ssh-key']) {
                            sh """
                                scp -o StrictHostKeyChecking=no kong.yml ec2-user@44.222.204.57:/home/ec2-user/kong.yml
                                ssh -o StrictHostKeyChecking=no ec2-user@44.222.204.57 "docker exec kong kong reload"
                            """
                        }
                    } else {
                        echo "Skipping deployment step (BUILD_TYPE = ${BUILD_TYPE})"
                    }
                }
            }
        }


        stage('Post Actions') {
            echo "Post-build actions completed for ${BUILD_TYPE}"
        }
    }
}

return this
