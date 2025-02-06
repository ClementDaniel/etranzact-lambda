pipeline {
    agent {
        docker {
            image 'paketobuildpacks/quarkus:latest' // Use the public Quarkus image from DockerHub
            // No sudo is used; everything runs as the container's default user.
        }
    }

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
                    def javaHome = tool name: 'JDK 17', type: 'jdk'  // Ensure JDK is installed in Jenkins
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }
        stage('Build & Test') {
    steps {
        withMaven(maven: 'Maven 3.8.6') {
            sh 'mvn clean package'
        }
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

