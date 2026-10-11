package com.quanto_lucrei.diagnosis.product.infrastructure.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.application.port.out.UnreliableProductFileHandler;

@Component
public class UnreliableProductFileHandlerImpl implements UnreliableProductFileHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			UnreliableProductFileHandlerImpl.class
	);

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

		// TODO Persist the original file and classification details for manual analysis.
	}
}
