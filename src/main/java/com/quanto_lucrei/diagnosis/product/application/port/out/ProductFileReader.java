package com.quanto_lucrei.diagnosis.product.application.port.out;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ProductFileReader {

	List<List<String>> read(MultipartFile file);
}
