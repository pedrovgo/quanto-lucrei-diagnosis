package com.quanto_lucrei.diagnosis.product.application.port.in;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.domain.Product;

public interface ProductImportService {

	List<Product> importProducts(MultipartFile file);
}
