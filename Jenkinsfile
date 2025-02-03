pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'  // Change to your AWS region
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // Replace with your S3 bucket
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/ClementDaniel/etranzact-lambda.git'
            }
        }

        stage('Set Up Java & Maven') {
            steps {
                script {
                    def javaHome = tool name: 'jdk-17', type: 'jdk'  // Ensure JDK 17 is installed
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Prepare Build Environment') {
            steps {
                sh 'chmod +x mvnw'  // Ensure Maven Wrapper is executable
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    sh './mvnw compile quarkus:dev & sleep 30'  // Run Quarkus dev mode for 30s
                    sh './mvnw clean package'  // Package the application
                }
            }
        }

        stage('Install AWS SAM CLI') {
            steps {
                sh '''
                    if ! command -v sam &> /dev/null; then
                        echo "AWS SAM CLI not found. Installing..."
                        if command -v yum &> /dev/null; then
                            sudo yum update -y
                            sudo yum install -y aws-sam-cli
                        elif command -v apt-get &> /dev/null; then
                            sudo apt-get update
                            sudo apt-get install -y aws-sam-cli
                        else
                            echo "No supported package manager found!"
                            exit 1
                        fi
                    else
                        echo "AWS SAM CLI already installed."
                    fi
                    sam --version
                '''
            }
        }

        stage('Build with SAM') {
            steps {
                sh '''
                    sam build --use-container
                '''
            }
        }

        stage('Package & Upload to S3') {
            steps {
                sh '''
                    sam package --s3-bucket $S3_BUCKET --output-template-file packaged.yaml
                '''
            }
        }

        stage('Deploy to AWS Lambda') {
            steps {
                sh '''
                    sam deploy --template-file packaged.yaml --stack-name $AWS_LAMBDA_FUNCTION_NAME --capabilities CAPABILITY_IAM --region $AWS_REGION
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed.'
        }
    }
}
