pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'  // Change this to your AWS region
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'  // Change this to your Lambda function name
        IMAGE_URI = 'docker.io/paketobuildpacks/quarkus:latest'
    }

    stages {
        stage('Install sudo on Jenkins Agent') {
            steps {
                script {
                    sh '''
                    echo "Installing sudo on Jenkins agent..."
                    docker exec -it jenkins-agent bash -c "apt-get update && apt-get install -y sudo"
                    '''
                }
            }
        }

        stage('Deploy to AWS Lambda') {
            steps {
                script {
                    sh '''
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
