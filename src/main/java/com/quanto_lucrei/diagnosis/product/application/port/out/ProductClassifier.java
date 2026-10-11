package com.quanto_lucrei.diagnosis.product.application.port.out;

import java.util.List;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;

public interface ProductClassifier {

	ProductColumnMapping classify(List<List<String>> rows);
}
