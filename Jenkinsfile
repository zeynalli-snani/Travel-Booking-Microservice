pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'java11'
        dockerTool 'docker'
    }
    environment {
        DOCKER_USER = 'iamnotsnani'
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
    }
    stages {
        stage('Quality Gate') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/jacoco/jacoco.xml, **/target/site/jacoco/index.html'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['discovery-service', 'api-gateway', 'flight-service', 'hotel-service', 'car-rental-service']

                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER_VAR')]) {
                        // Secure login
                        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER_VAR} --password-stdin"

                        for (service in services) {
                            echo "Processing ${service}..."
                            sh "docker build -t ${DOCKER_USER}/${service}:latest ./${service}"
                            sh "docker push ${DOCKER_USER}/${service}:latest"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment Pipeline finished successfully!'
        }
    }
}

