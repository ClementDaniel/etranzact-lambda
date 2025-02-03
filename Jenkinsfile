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
                sh '''
                    chmod +x mvnw  # Ensure the Maven Wrapper is executable
                '''
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

        stage('Install AWS CLI') {
            steps {
                sh '''
                    if ! command -v aws &> /dev/null; then
                        echo "AWS CLI not found. Installing..."
                        sudo apt update
                        sudo apt install -y awscli
                    else
                        echo "AWS CLI already installed."
                    fi
                    aws --version
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

        stage('Deploy to AWS Lambda') {
            steps {
                sh '''
                    ARTIFACT=$(ls target/*.jar | head -n 1)
                    aws lambda update-function-code --function-name $AWS_LAMBDA_FUNCTION_NAME --s3-bucket $S3_BUCKET --s3-key $(basename $ARTIFACT)
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
