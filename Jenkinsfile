pipeline {
    agent any
    environment {
        AWS_REGION = 'us-east-1'
        AWS_LAMBDA_FUNCTION_NAME = 'etranzactFunction'
        S3_BUCKET = 'etranzact'
        SAM_INSTALL_DIR = "${WORKSPACE}/.sam-cli"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/ClementDaniel/etranzact-lambda.git', 
                    branch: 'main'
            }
        }

        stage('Setup Java') {
            steps {
                script {
                    def javaHome = tool name: 'jdk-17', type: 'jdk'
                    env.PATH = "${javaHome}/bin:${env.PATH}"
                }
                sh 'java -version'
            }
        }

        stage('Install AWS SAM') {
            steps {
                sh '''
                    # Clean previous installations
                    rm -rf aws-sam-cli-linux.zip sam-installation "${SAM_INSTALL_DIR}"
                    
                    if ! command -v sam &> /dev/null; then
                        echo "=== Installing AWS SAM CLI ==="
                        curl -Lo aws-sam-cli-linux.zip https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
                        unzip -o aws-sam-cli-linux.zip -d sam-installation
                        
                        # Install to workspace directory
                        mkdir -p "${SAM_INSTALL_DIR}"
                        ./sam-installation/install --install-dir "${SAM_INSTALL_DIR}" --update-path
                        
                        echo "=== SAM CLI installed to ${SAM_INSTALL_DIR} ==="
                    else
                        echo "=== SAM CLI already installed ==="
                    fi
                '''
                script {
                    env.PATH = "${env.SAM_INSTALL_DIR}/dist:${env.PATH}"
                }
                sh 'sam --version'
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                    echo "=== Building Application ==="
                    chmod +x mvnw
                    ./mvnw clean package
                    
                    echo "=== Running Tests ==="
                    ./mvnw test
                '''
            }
        }

        stage('SAM Deploy') {
            steps {
                sh '''
                    echo "=== Building SAM Application ==="
                    sam build --use-container --template template.yaml
                    
                    echo "=== Deploying to AWS ==="
                    sam deploy \
                        --region ${AWS_REGION} \
                        --stack-name ${AWS_LAMBDA_FUNCTION_NAME} \
                        --s3-bucket ${S3_BUCKET} \
                        --capabilities CAPABILITY_IAM \
                        --no-confirm-changeset \
                        --no-fail-on-empty-changeset
                '''
            }
        }
    }

    post {
        always {
            sh '''
                echo "=== Cleaning up ==="
                rm -rf aws-sam-cli-linux.zip sam-installation
            '''
        }
        success {
            slackSend color: 'good', message: "Deployment succeeded: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger', message: "Deployment failed: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
            archiveArtifacts artifacts: '**/target/*.log', allowEmptyArchive: true
        }
    }
}
