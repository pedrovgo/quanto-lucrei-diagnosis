package com.quanto_lucrei.diagnosis.product.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;

public interface ProductReviewStorage {

	void store(
			MultipartFile file,
			ProductColumnMapping mapping,
			String reason
	);
}
