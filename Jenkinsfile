pipeline {
    agent {
        docker { image 'python:3.9' }  // Use a Python 3.9 image
    }
    
    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'
        SAM_CLI_PATH = "$HOME/.local/bin/sam"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/ClementDaniel/etranzact-lambda.git'
            }
        }

        stage('Install AWS SAM CLI via pip') {
            steps {
                sh '''
                    # Ensure pip is installed
                    python3 -m ensurepip --default-pip
                    python3 -m pip install --upgrade pip

                    # Install AWS SAM CLI
                    python3 -m pip install --user aws-sam-cli

                    # Verify AWS SAM installation
                    $HOME/.local/bin/sam --version
                '''
                script {
                    env.PATH = "$HOME/.local/bin:$PATH"
                }
            }
        }

        stage('Build & Deploy with AWS SAM') {
            steps {
                sh '''
                    export PATH="$HOME/.local/bin:$PATH"
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
