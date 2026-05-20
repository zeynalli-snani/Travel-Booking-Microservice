pipeline {
    agent any

    tools {
            maven 'maven'
            jdk 'java11'
            dockerTool 'docker'
        }

    environment {
        DOCKER_USER = 'iamnotsnani'
    }

    stages {

        stage('Maven Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build & Tag') {
            steps {
                script {
                    def services = ['discovery-service', 'api-gateway', 'flight-service', 'hotel-service', 'car-rental-service']

                    for (service in services) {
                        echo "Building image for ${service}..."
                        sh "docker build -t ${DOCKER_USER}/${service}:latest ./${service}"
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER_VAR')]) {
                        sh "docker login -u ${DOCKER_USER_VAR} -p ${DOCKER_PASS}"

                        def services = ['discovery-service', 'api-gateway', 'flight-service', 'hotel-service', 'car-rental-service']
                        for (service in services) {
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