package com.quanto_lucrei.diagnosis.product.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.domain.Product;
import com.quanto_lucrei.diagnosis.product.exception.InvalidProductFileException;
import com.quanto_lucrei.diagnosis.product.application.assembler.ProductAssembler;
import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.application.port.in.ProductImportService;
import com.quanto_lucrei.diagnosis.product.application.port.out.ProductClassifier;
import com.quanto_lucrei.diagnosis.product.application.port.out.ProductFileReader;
import com.quanto_lucrei.diagnosis.product.application.port.out.UnreliableProductFileHandler;

@Service
public class ProductImportServiceImpl implements ProductImportService {

	private final ProductFileReader fileReader;
	private final ProductClassifier productClassifier;
	private final ProductAssembler productAssembler;
	private final UnreliableProductFileHandler unreliableProductFileHandler;

	public ProductImportServiceImpl(
			ProductFileReader fileReader,
			ProductClassifier productClassifier,
			ProductAssembler productAssembler,
			UnreliableProductFileHandler unreliableProductFileHandler
	) {
		this.fileReader = fileReader;
		this.productClassifier = productClassifier;
		this.productAssembler = productAssembler;
		this.unreliableProductFileHandler = unreliableProductFileHandler;
	}

	@Override
	public List<Product> importProducts(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new InvalidProductFileException("The uploaded file must not be empty");
		}

		List<List<String>> rows = fileReader.read(file);
		if (rows.size() < 2) {
			ProductColumnMapping mapping = new ProductColumnMapping(Map.of(), Map.of());
			unreliableProductFileHandler.handle(
					file,
					mapping,
					"The product file does not contain a header and at least one data row"
			);
			return List.of();
		}

		ProductColumnMapping mapping = productClassifier.classify(rows);
		if (!mapping.isReliable()) {
			String reason = mapping.reliabilityReason();
			unreliableProductFileHandler.handle(file, mapping, reason);
			return List.of();
		}
		return productAssembler.assemble(rows, mapping);
	}
}
