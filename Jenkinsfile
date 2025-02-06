pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'
        IMAGE_URI = 'docker.io/paketobuildpacks/quarkus:latest'
    }

    stages {
        stage('Deploy to AWS Lambda') {
            steps {
                script {
                    sh '''
                    echo "Deploying public DockerHub image to AWS Lambda..."
                    aws lambda update-function-code \
                        --function-name $LAMBDA_FUNCTION_NAME \
                        --image-uri $IMAGE_URI
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deployment to AWS Lambda was successful!'
        }
        failure {
            echo '❌ Deployment failed. Check logs for details.'
        }
    }
}

