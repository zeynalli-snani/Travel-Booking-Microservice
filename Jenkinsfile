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
        EC2_HOST = '3.71.152.215'
        EC2_DEPLOY_PATH = '/home/ubuntu/travel-booking'
    }
    stages {
        stage('Quality Gate') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco()
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

        stage('Deploy To EC2') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    sh '''
                        chmod 600 "$SSH_KEY"
                        ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no ${SSH_USER}@${EC2_HOST} "mkdir -p ${EC2_DEPLOY_PATH}"
                        scp -i "$SSH_KEY" -o StrictHostKeyChecking=no docker-compose.yml ${SSH_USER}@${EC2_HOST}:${EC2_DEPLOY_PATH}/docker-compose.yml
                        ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no ${SSH_USER}@${EC2_HOST} "
                            cd ${EC2_DEPLOY_PATH} &&
                            sudo docker compose pull &&
                            sudo docker compose up -d &&
                            sudo docker ps
                        "
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Deployment Pipeline finished successfully! Application should be available at http://${EC2_HOST}:8080"
        }
    }
}
