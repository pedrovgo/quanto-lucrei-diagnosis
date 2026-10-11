package com.quanto_lucrei.diagnosis.product.infrastructure.review;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.application.port.out.UnreliableProductFileHandler;

@Component
public class UnreliableProductFileHandlerImpl implements UnreliableProductFileHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			UnreliableProductFileHandlerImpl.class
	);

	private final S3Client s3Client;
	private final String bucket;

	public UnreliableProductFileHandlerImpl(
			S3Client s3Client,
			@Value("${product.review.s3.bucket}") String bucket
	) {
		this.s3Client = s3Client;
		this.bucket = bucket;
	}

	@Override
	public void handle(
			MultipartFile file,
			ProductColumnMapping mapping,
			String reason
	) {
		LOGGER.warn(
				"Product file sent for manual review: file={}, reason={}, columns={}, confidences={}",
				file.getOriginalFilename(),
				reason,
				mapping.columns(),
				mapping.confidences()
		);

		try {
			String key = buildObjectKey(file);
			Map<String, String> metadata = new HashMap<>();
			metadata.put("review-reason", reason);
			metadata.put("classification-columns", mapping.columns().toString());
			metadata.put("classification-confidences", mapping.confidences().toString());

			PutObjectRequest request = PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.contentType(resolveContentType(file))
					.metadata(metadata)
					.build();

			s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
			LOGGER.info("Product file persisted for manual review: bucket={}, key={}", bucket, key);
		} catch (IOException exception) {
			throw new IllegalStateException(
					"Could not read product file for manual review",
					exception
			);
		} catch (SdkException exception) {
			throw new IllegalStateException(
					"Could not persist product file for manual review in S3",
					exception
			);
		}
	}

	private String buildObjectKey(MultipartFile file) {
		String filename = file.getOriginalFilename();
		String safeFilename = filename == null || filename.isBlank()
				? "unnamed-file"
				: filename.replaceAll("[^a-zA-Z0-9._-]", "_");
		return "unreliable-products/"
				+ Instant.now()
				+ "/"
				+ UUID.randomUUID()
				+ "-"
				+ safeFilename;
	}

	private String resolveContentType(MultipartFile file) {
		String contentType = file.getContentType();
		return contentType == null || contentType.isBlank()
				? "application/octet-stream"
				: contentType;
	}
}
