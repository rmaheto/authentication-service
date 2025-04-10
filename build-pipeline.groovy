def runPipeline(Map config) {
    node(config.agent ?: 'Linux-RHEL7-Shared-BuildAgent') {
        def SERVICE_NAME = config.serviceName
        def GIT_BRANCH = config.branch
        def GIT_REPO = config.repo
        def BUILD_TYPE = config.buildType
        def VERSION
        def PROPS = config.props

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

        dir(SERVICE_NAME) {  // 👈 Wrap all project file references in this block
            stage('Setup') {
                echo '🧹 Cleaning environment...'
                sh 'ls -la && pwd'  // Debug: See current path
                sh 'mvn clean'

                if (!PROPS['SOLUTION_ID'] || !PROPS['APPLICATION']) {
                    error "❌ Missing required fields in input.json (SOLUTION_ID or APPLICATION)"
                }
            }

            stage('Build') {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    VERSION = "${pom.version}.${env.BUILD_NUMBER}"
                    env.VERSION = VERSION

                    echo "🧪 Running tests..."
                    sh 'mvn test'

                    echo "🏗️  Building version: ${VERSION}"
                    sh 'mvn install'
                }
            }

            stage('Publish') {
                when {
                    expression { ['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString()) }
                }
                steps {
                    echo "📤 Publishing artifact version ${VERSION}..."
                    sh "echo Simulating publish of artifact version ${VERSION}"
                }
            }

            stage('Tag') {
                when {
                    expression { ['build_publish', 'build_publish_deploy'].contains(BUILD_TYPE.toString()) }
                }
                steps {
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

            stage('Deploy') {
                when {
                    expression { BUILD_TYPE.toString() == 'build_publish_deploy' }
                }
                steps {
                    echo "🚀 Deploying application..."
                    sshagent(credentials: ['kong-server-ssh-key']) {
                        sh """
                            scp -o StrictHostKeyChecking=no kong.yml ec2-user@44.222.204.57:/home/ec2-user/kong.yml
                            ssh -o StrictHostKeyChecking=no ec2-user@44.222.204.57 "docker exec kong kong reload"
                        """
                    }
                }
            }
        }

        stage('Post Actions') {
            echo "✅ Pipeline complete for ${SERVICE_NAME} (${BUILD_TYPE})"
        }
    }
}

return this
