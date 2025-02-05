pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'  // Change this to your AWS region
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'  // Change this to your Lambda function name
        IMAGE_URI = 'docker.io/paketobuildpacks/quarkus:latest'   
    }

    stages {
        stage('Install AWS CLI') {
            steps {
                sh '''
                    if ! command -v aws &> /dev/null; then
                        echo "Installing AWS CLI..."
                        curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                        unzip awscliv2.zip
                        # sudo ./aws/install
                        echo "AWS CLI installed successfully."
                    else
                        echo "AWS CLI already installed."
                    fi
                    aws --version
                '''
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
            echo 'Deployment to AWS Lambda was successful!'
        }
        failure {
            echo 'Deployment failed. Check logs for details.'
        }
    }
}
