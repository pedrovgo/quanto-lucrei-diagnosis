package com.quanto_lucrei.diagnosis.product.application.assembler;

import java.util.List;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.domain.Product;

public interface ProductAssembler {

	List<Product> assemble(
			List<List<String>> rows,
			ProductColumnMapping mapping
	);
}
