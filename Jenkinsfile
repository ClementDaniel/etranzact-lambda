pipeline {
    agent any  // Runs on any available Jenkins agent

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // Replace with your S3 bucket
        SAM_CLI_PATH = '/usr/local/bin/sam'  // Path to AWS SAM CLI
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

        stage('Install AWS SAM CLI') {
            steps {
                sh '''
                    if ! command -v sam &> /dev/null; then
                        echo "Installing AWS SAM CLI..."
                        curl -Lo aws-sam-cli-linux.zip https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
                        unzip aws-sam-cli-linux.zip -d sam-installation
                        ./sam-installation/install
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

        stage('Build & Test Application') {
            steps {
                sh '''
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw clean package  # Build the application
                '''
            }
        }

        stage('Build & Deploy with AWS SAM (Docker)') {
            agent { docker { image 'public.ecr.aws/sam/build-java17' } }
            steps {
                sh '''
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
