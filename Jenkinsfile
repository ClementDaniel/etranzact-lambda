pipeline {
    agent {
        docker {
            image 'paketobuildpacks/quarkus:latest' // Use the public Quarkus image from DockerHub
            // No sudo is used; everything runs as the container's default user.
        }
    }

    environment {
        AWS_REGION = 'us-east-1'                      // Your AWS region
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'  // Your AWS Lambda function name
        IMAGE_URI = 'docker.io/paketobuildpacks/quarkus:latest' // Public DockerHub image URI
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/ClementDaniel/etranzact-lambda.git'
            }
        }

        stage('Build Application') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw clean package
                '''
            }
        }

        stage('Prepare Dependencies') {
            steps {
                sh '''
                    # (Optional) Use Maven dependency:copy-dependencies goal to copy all dependencies into target/dependency/
                    ./mvnw dependency:copy-dependencies -DoutputDirectory=target/dependency
                '''
            }
        }

        stage('Deploy to AWS Lambda') {
            steps {
                script {
                    // Compile and run the deployment class using the AWS SDK.
                    // Adjust the classpath as needed (here it uses target/dependency/* for external libraries)
                    sh '''
                        javac -cp "target/dependency/*:." deployment/LambdaDeployer.java
                        java -cp "target/dependency/*:." LambdaDeployer
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
