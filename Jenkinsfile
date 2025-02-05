pipeline {
    agent {
        docker {
            image 'public.ecr.aws/sam/build-java17'  // Pre-configured with Java 17 and SAM CLI
            args '-u root'  // Optional: Ensure proper permissions for Maven wrapper
        }
    }

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // Replace with your S3 bucket
    }

    stages {
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/ClementDaniel/etranzact-lambda.git',
                    branch: 'main'  // Specify your target branch
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'  // Runs compile, test, and package phases
            }
        }

        stage('SAM Deployment') {
            steps {
                sh 'sam build --use-container'  // Build in Docker container for consistency
                sh 'sam deploy --no-confirm-changeset --no-fail-on-empty-changeset'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed - cleaning up workspace'
        }
        success {
            echo '✅ Deployment successful!'
        }
        failure {
            echo '❌ Deployment failed - check pipeline logs'
            // Add notification steps here (Slack, email, etc.)
        }
    }
}
