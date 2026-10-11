package com.quanto_lucrei.diagnosis.product.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.application.port.in.ProductImportService;
import com.quanto_lucrei.diagnosis.product.domain.Product;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductImportService productImportService;

	public ProductController(ProductImportService productImportService) {
		this.productImportService = productImportService;
	}

	@PostMapping(
			path = "/import",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public List<Product> importFile(@RequestParam("file") MultipartFile file) {
		return productImportService.importProducts(file);
	}
}
