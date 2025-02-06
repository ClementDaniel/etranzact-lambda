package com.deployment;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeRequest;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeResponse;

public class LambdaDeployer {
    public static void main(String[] args) {
        // Read parameters from environment variables
        String functionName = System.getenv("LAMBDA_FUNCTION_NAME"); 
        String imageUri = System.getenv("IMAGE_URI"); 
        String region = System.getenv().getOrDefault("AWS_REGION", "us-east-1");
        String roleArn = System.getenv("LAMBDA_ROLE"); // The IAM Role ARN for Lambda execution

        if (functionName == null || imageUri == null || roleArn == null) {
            System.err.println("LAMBDA_FUNCTION_NAME, IMAGE_URI, and LAMBDA_ROLE must be provided as environment variables.");
            System.exit(1);
        }

        try (LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            try {
                // Try to update the function code if the function already exists
                UpdateFunctionCodeRequest updateRequest = UpdateFunctionCodeRequest.builder()
                        .functionName(functionName)
                        .imageUri(imageUri)
                        .build();

                UpdateFunctionCodeResponse updateResponse = lambdaClient.updateFunctionCode(updateRequest);
                System.out.println("Deployment successful (update): " + updateResponse.toString());
            } catch (ResourceNotFoundException rnfe) {
                System.out.println("Function not found. Creating a new Lambda function...");
                // Create a new Lambda function with container image package type
                CreateFunctionRequest createRequest = CreateFunctionRequest.builder()
                        .functionName(functionName)
                        .packageType("Image")
                        .code(c -> c.imageUri(imageUri))
                        .role(roleArn)
                        .build();

                CreateFunctionResponse createResponse = lambdaClient.createFunction(createRequest);
                System.out.println("Deployment successful (create): " + createResponse.toString());
            }
        } catch (Exception e) {
            System.err.println("Deployment failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
