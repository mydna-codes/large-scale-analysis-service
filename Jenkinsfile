pipeline {

    agent any

    environment {
        // Global variables
        KUBERNETES_CREDENTIALS = "k8s-kubeconfig"
        NEXUS_CREDENTIALS      = "mydnacodes-nexus-user"
        DOCKER_CREDENTIALS     = "mydnacodes-docker-user"
        // Local variables
        DOCKER_IMAGE_TAG       = "mydnacodes/large-scale-analysis"
        DOCKER_IMAGE           = ""
        COMMIT_AUTHOR          = ""
        COMMIT_MESSAGE         = ""
        PROJECT_VERSION        = ""
    }

    tools {
        maven "mvn-3.6"
        jdk "jdk-11"
    }

    stages {
        stage("Set environment variables") {
            steps {
                script {
                    pom                  = readMavenPom file:"pom.xml"
                    PROJECT_VERSION      = pom.version
                    COMMIT_MESSAGE       = sh script: "git show -s --pretty='%s'", returnStdout: true
                    COMMIT_AUTHOR        = sh script: "git show -s --pretty='%cn <%ce>'", returnStdout: true
                    COMMIT_AUTHOR        = COMMIT_AUTHOR.trim()
                }
            }
        }
        stage("Package application") {
            steps {
                sh "mvn clean package -DskipTests=true"
            }
        }
        stage("Build docker image") {
            steps {
                script {
                    dockerImage = docker.build DOCKER_IMAGE_TAG
                }
            }
        }
        stage("Publish docker image") {
            steps {
                script {
                    docker.withRegistry("", DOCKER_CREDENTIALS) {
                        dockerImage.push("$PROJECT_VERSION")
                        dockerImage.push("latest")
                    }
                }
            }
        }
        stage("Clean docker images") {
            steps {
                sh "docker rmi $DOCKER_IMAGE_TAG:$PROJECT_VERSION"
                sh "docker rmi $DOCKER_IMAGE_TAG:latest"
            }
        }
        stage("Deploy libraries") {
           steps {
               withCredentials([usernamePassword(credentialsId: NEXUS_CREDENTIALS, passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                   sh "mvn clean deploy -DskipTests=true -Dnexus.username=$USERNAME -Dnexus.password=$PASSWORD --settings .ci/settings.xml -P lib"
               }
           }
        }
        stage("Clean maven packages") {
            steps {
                sh "mvn clean"
            }
        }
        stage("Prepare deployments") {
            steps {
                script {
                    def deploymentConfig = readYaml file: ".ci/deployment-config.yaml"
                    def environment      = ""

                    if (env.GIT_BRANCH.equals("prod") || env.GIT_BRANCH.equals("origin/prod")) {
                        environment = deploymentConfig.environments.prod
                    } else {
                        environment = deploymentConfig.environments.dev
                    }

                    sh """ \
                    sed -e 's+{{IMAGE_NAME}}+$DOCKER_IMAGE_TAG:$PROJECT_VERSION+g' \
                        -e 's+{{NAMESPACE}}+$environment.namespace+g' \
                        -e 's+{{ENV_SUFFIX}}+$environment.suffix+g' \
                        -e 's+{{VERSION}}+$PROJECT_VERSION+g' \
                        -e 's+{{ENV_NAME}}+$environment.name+g' \
                        -e 's+{{ENV_PROD}}+$environment.prod+g' \
                        .kube/large-scale-analysis.yaml > .kube/large-scale-analysis.tmp
                    """
                    sh "mv -f .kube/large-scale-analysis.tmp .kube/large-scale-analysis.yaml"
                }
            }
        }
        stage("Deploy application") {
            steps {

                withKubeConfig([credentialsId: KUBERNETES_CREDENTIALS]) {
                    sh "kubectl apply -f .kube/large-scale-analysis.yaml"
                }
            }
        }
    }
    post {
        success {
            slackSend (color: '#57BA57',
                       message: """[<${env.BUILD_URL}|Build ${env.BUILD_NUMBER}>] *SUCCESSFUL*\n
                                  |Version: `${PROJECT_ARTIFACT_ID}:${PROJECT_VERSION}`\n
                                  |Branch:  *${GIT_BRANCH}*
                                  |Author:  ${COMMIT_AUTHOR}
                                  |Message: ${COMMIT_MESSAGE}""".stripMargin()
            )
        }
        failure {
            slackSend (color: '#BD0808',
                       message: """[<${env.BUILD_URL}|Build ${env.BUILD_NUMBER}>] *FAILED*\n
                                  |Job: *${env.JOB_NAME}*\n
                                  |Branch: ${GIT_BRANCH}
                                  |Author: ${COMMIT_AUTHOR}
                                  |Message: ${COMMIT_MESSAGE}""".stripMargin()
            )
        }
    }
}