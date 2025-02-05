pipeline {
    agent {
        docker {
            image 'quarkus/ubi-quarkus-mandrel:22.3-java17'  // Use Quarkus build image
            args '--user root'  // Ensure root access for dependencies
        }
    }

    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'  // S3 bucket for deployment
        DEPLOYMENT_PACKAGE = 'target/function.zip'  // Output ZIP file
    }

    stages {
        stage('Checkout Code') {
            steps {
                git 'https://github.com/ClementDaniel/etranzact-lambda.git'
            }
        }

        stage('Build Quarkus Lambda Function') {
            steps {
                sh '''
                    chmod +x mvnw  # Ensure Maven Wrapper is executable
                    ./mvnw clean package -Dnative -Dquarkus.native.container-build=true  # Build native image
                '''
            }
        }

        stage('Prepare Deployment Package') {
            steps {
                sh '''
                    echo "Zipping deployment package..."
                    zip -j $DEPLOYMENT_PACKAGE target/*-runner target/lib/*
                '''
            }
        }

        stage('Deploy to AWS Lambda via SDK') {
            steps {
                script {
                    def awsLambdaUpdateScript = '''
                    import boto3

                    AWS_REGION = "us-east-1"
                    LAMBDA_FUNCTION_NAME = "etranzactFunction"
                    ZIP_FILE_PATH = "target/function.zip"

                    client = boto3.client('lambda', region_name=AWS_REGION)

                    with open(ZIP_FILE_PATH, 'rb') as f:
                        zip_content = f.read()

                    response = client.update_function_code(
                        FunctionName=LAMBDA_FUNCTION_NAME,
                        ZipFile=zip_content
                    )

                    print("Deployment complete:", response)
                    '''

                    writeFile(file: 'deploy_lambda.py', text: awsLambdaUpdateScript)
                    sh 'python3 deploy_lambda.py'  // Run the Python script
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deployment successful!'
        }
        failure {
            echo '❌ Deployment failed. Check logs for details.'
        }
    }
}
