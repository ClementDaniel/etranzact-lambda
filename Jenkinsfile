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
            echo 'Deployment failed.'
        }
    }
}
