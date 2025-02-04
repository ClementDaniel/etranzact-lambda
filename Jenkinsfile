pipeline {
    agent any  

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

        stage('Install Java & Maven') {
            steps {
                script {
                    def javaHome = tool name: 'jdk-17', type: 'jdk'
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Install Python, pip & AWS SAM CLI') {
            steps {
                sh '''
                    # Ensure Python 3 is installed
                    if ! command -v python3 &> /dev/null; then
                        echo "Installing Python 3..."
                        apt update && apt install -y python3 python3-pip
                    fi

                    # Ensure pip is installed
                    if ! command -v pip &> /dev/null; then
                        echo "Installing pip..."
                        python3 -m ensurepip --default-pip
                        python3 -m pip install --upgrade pip
                    fi

                    # Install AWS SAM CLI if not installed
                    if ! command -v sam &> /dev/null; then
                        echo "Installing AWS SAM CLI using pip..."
                        python3 -m pip install --user aws-sam-cli
                        echo "AWS SAM installed successfully."
                    else
                        echo "AWS SAM already installed."
                    fi

                    # Verify AWS SAM installation
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
                    chmod +x mvnw  
                    ./mvnw clean package  
                '''
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
