package com.project.G1_T3.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSecretsConfig {

    @Bean
    public AWSSecretsManager awsSecretsManager() {
        return AWSSecretsManagerClientBuilder.standard().build();
    }

    @Bean
    public void setDatabaseSecret() throws Exception {

        AWSSecretsManager client = awsSecretsManager();
        String secretName = "rds!cluster-8210d2ac-0455-472b-9b71-6f9eca214f5f";

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        String secret = getSecretValueResult.getSecretString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode secretJson = objectMapper.readTree(secret);

        System.setProperty("rds.url", secretJson.get("host").asText());
        System.setProperty("rds.username", secretJson.get("username").asText());
        System.setProperty("rds.password", secretJson.get("password").asText());
        System.setProperty("jwt.secret", secretJson.get("jwtSecret").asText());
        
    }
}