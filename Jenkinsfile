pipeline {
    agent {
        docker {
            image 'paketobuildpacks/quarkus:latest' // Use the Quarkus build image
        }
    }

    environment {
        AWS_REGION = 'us-east-1'  // Change this to your AWS region
        LAMBDA_FUNCTION_NAME = 'quarkusLambdaFunction'  // Change this to your Lambda function name
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
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw clean package  # Package the application
                '''
            }
        }

        stage('Deploy to AWS Lambda') {
            steps {
                script {
                    def lambdaCode = """
                        import software.amazon.awssdk.services.lambda.LambdaClient;
                        import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeRequest;

                        public class LambdaDeployer {
                            public static void main(String[] args) {
                                LambdaClient lambdaClient = LambdaClient.create();
                                UpdateFunctionCodeRequest updateRequest = UpdateFunctionCodeRequest.builder()
                                    .functionName("${env.LAMBDA_FUNCTION_NAME}")
                                    .imageUri("docker.io/paketobuildpacks/quarkus:latest")
                                    .build();
                                lambdaClient.updateFunctionCode(updateRequest);
                                System.out.println("Deployment successful!");
                            }
                        }
                    """.stripIndent()

                    writeFile file: 'LambdaDeployer.java', text: lambdaCode

                    sh '''
                        javac -cp . LambdaDeployer.java  # Compile Java deployment script
                        java -cp . LambdaDeployer  # Run Java deployment script
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
