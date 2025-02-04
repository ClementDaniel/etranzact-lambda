pipeline {
    agent any  // Runs on any available Jenkins agent

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'
        SAM_CLI_PATH = "$HOME/.local/bin/sam"  // AWS SAM CLI Path
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

        stage('Install Python & AWS SAM') {
            steps {
                sh '''
                    # Install Python 3 if missing
                    if ! command -v python3 &> /dev/null; then
                        echo "Python 3 is missing! Install it manually on the Jenkins agent."
                        exit 1
                    fi

                    # Ensure pip is installed
                    python3 -m ensurepip --default-pip
                    python3 -m pip install --upgrade pip
                    
                    # Install AWS SAM CLI if missing
                    if ! command -v sam &> /dev/null; then
                        echo "Installing AWS SAM CLI..."
                        python3 -m pip install --user aws-sam-cli
                        echo "AWS SAM installed successfully."
                    else
                        echo "AWS SAM already installed."
                    fi

                    # Verify AWS SAM installation
                    $HOME/.local/bin/sam --version
                '''
                script {
                    env.PATH = "$HOME/.local/bin:$PATH"
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw clean package  # Build Java Lambda
                '''
            }
        }

        stage('Upload to S3') {
            steps {
                sh '''
                    ARTIFACT=$(ls target/*.jar | head -n 1)
                    aws s3 cp $ARTIFACT s3://$S3_BUCKET/
                    echo "Uploaded $ARTIFACT to S3"
                '''
            }
        }

        stage('Deploy with AWS SAM') {
            steps {
                sh '''
                    export PATH="$HOME/.local/bin:$PATH"  # Ensure Jenkins finds SAM
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



