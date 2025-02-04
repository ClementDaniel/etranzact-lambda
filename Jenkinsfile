pipeline {
    agent any  // Runs on any available Jenkins agent

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // Replace with your S3 bucket
        SAM_CLI_PATH = "$HOME/.local/bin/sam"  // Install SAM CLI in a user directory
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

        stage('Install AWS SAM CLI (via pip)') {
            steps {
                sh '''
                    if ! command -v sam &> /dev/null; then
                        echo "Installing AWS SAM CLI using pip..."
                        pip install --user aws-sam-cli
                        echo "AWS SAM installed successfully."
                    else
                        echo "AWS SAM already installed."
                    fi
                    $HOME/.local/bin/sam --version
                '''
                script {
                    env.PATH = "$HOME/.local/bin:$PATH"  // Ensure Jenkins finds SAM CLI
                }
            }
        }

        stage('Build & Test Application') {
            steps {
                sh '''
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw clean package  # Build the application
                    ./mvnw clean package'  // Package the application
                '''
            }
        }

        stage('Build & Deploy with AWS SAM') {
            steps {
                sh '''
                    export PATH="$HOME/.local/bin:$PATH"  # Ensure SAM CLI is found
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
