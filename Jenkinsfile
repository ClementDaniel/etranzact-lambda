

pipeline {
    agent any  // Runs on any available Jenkins agent

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // Replace with your S3 bucket
        SAM_CLI_PATH = '/usr/local/bin/sam'  // Set default AWS SAM path
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
                    def javaHome = tool name: 'jdk-17', type: 'jdk'  // Ensure JDK 17 is installed
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Install AWS SAM') {
            steps {
                sh '''
                    if ! command -v sam &> /dev/null; then
                        echo "Installing AWS SAM CLI..."
                        curl -Lo aws-sam-cli-linux.zip https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
                        unzip aws-sam-cli-linux.zip -d sam-installation
                        sudo ./sam-installation/install
                        echo "AWS SAM installed successfully."
                    else
                        echo "AWS SAM already installed."
                    fi
                    sam --version
                '''
                script {
                    env.PATH = "/usr/local/bin:$PATH"  // Ensure Jenkins finds SAM CLI
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw compile quarkus:dev & sleep 30  # Run Quarkus dev mode for 30s
                    ./mvnw clean package  # Package the application
                '''
            }
        }

        stage('Build & Deploy with SAM') {
            steps {
                sh '''
                    export PATH="/usr/local/bin:$PATH"  # Ensure Jenkins finds SAM
                    sam build
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
