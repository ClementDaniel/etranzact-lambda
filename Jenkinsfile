pipeline {
    agent any

    environment {
        # Load variables from .env file
        AWS_REGION = credentials('AWS_REGION')
        AWS_LAMBDA_FUNCTION_NAME = credentials('AWS_LAMBDA_FUNCTION_NAME')
        S3_BUCKET = credentials('S3_BUCKET')
        JDK_VERSION = credentials('JDK_VERSION')
        MAVEN_VERSION = credentials('MAVEN_VERSION')
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
                    def javaHome = tool name: "JDK ${JDK_VERSION}", type: 'jdk'
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Build & Test') {
            steps {
                withMaven(maven: "Maven ${MAVEN_VERSION}") {
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
