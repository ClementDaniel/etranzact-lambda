pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'
        IMAGE_URI = 'docker.io/paketobuildpacks/quarkus:latest'
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
    }

    stages {
        stage('Deploy to AWS Lambda Without AWS CLI') {
            steps {
                sh '''
                    STS_TOKEN=$(curl -s -X POST "https://sts.amazonaws.com/" \
                        -H "Content-Type: application/x-www-form-urlencoded" \
                        --data-urlencode "Action=GetCallerIdentity" \
                        --data-urlencode "Version=2011-06-15" \
                        --data-urlencode "AWSAccessKeyId=$AWS_ACCESS_KEY_ID" \
                        --data-urlencode "AWSSecretAccessKey=$AWS_SECRET_ACCESS_KEY")

                    curl -X PUT "https://lambda.$AWS_REGION.amazonaws.com/2015-03-31/functions/$LAMBDA_FUNCTION_NAME/code" \
                        -H "Content-Type: application/json" \
                        -H "Authorization: Bearer $STS_TOKEN" \
                        -d '{"ImageUri": "'$IMAGE_URI'"}'
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Deployment successful using AWS Lambda API!'
        }
        failure {
            echo '❌ Deployment failed. Check logs for details.'
        }
    }
}
