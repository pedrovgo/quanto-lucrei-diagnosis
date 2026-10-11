package com.quanto_lucrei.diagnosis.product.infrastructure.review;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3StorageConfiguration {

	@Bean
	S3Client s3Client(
			@Value("${product.review.s3.region}") String region,
			@Value("${product.review.s3.endpoint:}") String endpoint,
			@Value("${product.review.s3.path-style-access:false}") boolean pathStyleAccess,
			@Value("${product.review.s3.access-key}") String accessKey,
			@Value("${product.review.s3.secret-key}") String secretKey
	) {
		var builder = S3Client.builder()
				.region(Region.of(region))
				.serviceConfiguration(S3Configuration.builder()
						.pathStyleAccessEnabled(pathStyleAccess)
						.build());

		if (accessKey.isBlank() && secretKey.isBlank()) {
			builder.credentialsProvider(DefaultCredentialsProvider.create());
		} else if (accessKey.isBlank() || secretKey.isBlank()) {
			throw new IllegalStateException(
					"Both product.review.s3.access-key and product.review.s3.secret-key must be configured"
			);
		} else {
			builder.credentialsProvider(StaticCredentialsProvider.create(
					AwsBasicCredentials.create(accessKey, secretKey)
			));
		}

		if (!endpoint.isBlank()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		return builder.build();
	}
}
