pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/ClementDaniel/etranzact-lambda.git'
            }
        }

        stage('Install Java & Maven') {
            steps {
                script {
                    def javaHome = tool name: 'jdk-17', type: 'jdk'
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Install AWS SAM') {
            steps {
                sh '''
                    # Install AWS SAM CLI using Python pip (more reliable method)
                    python3 -m pip install --user aws-sam-cli
                    echo "Installed SAM version: $(~/.local/bin/sam --version)"
                '''
                script {
                    // Add user's local bin directory to PATH
                    env.PATH = "${env.HOME}/.local/bin:${env.PATH}"
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw clean verify
                '''
            }
        }

        stage('Build & Deploy with SAM') {
            steps {
                sh '''
                    sam build --use-container
                    sam deploy --no-confirm-changeset --no-fail-on-empty-changeset
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed. Check logs for details.'
        }
    }
}
