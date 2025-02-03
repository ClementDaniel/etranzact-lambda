pipeline {
    agent any
    agent { docker { image 'public.ecr.aws/sam/build-nodejs20.x' } }

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
                sh 'chmod +x mvnw'  // Ensure the Maven Wrapper is executable
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
