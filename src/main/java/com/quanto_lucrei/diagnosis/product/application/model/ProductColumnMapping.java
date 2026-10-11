package com.quanto_lucrei.diagnosis.product.application.model;

import java.util.Map;

public record ProductColumnMapping(
		Map<ProductField, Integer> columns,
		Map<ProductField, Double> confidences
) {

	public int columnOf(ProductField field) {
		return columns.getOrDefault(field, -1);
	}

	public boolean isReliable(double minimumConfidence) {
		return hasReliableField(ProductField.NAME, minimumConfidence)
				&& hasReliableField(ProductField.COST, minimumConfidence);
	}

	public String reliabilityReason(double minimumConfidence) {
		if (!hasReliableField(ProductField.NAME, minimumConfidence)) {
			return "The product name column was not identified with sufficient confidence";
		}
		if (!hasReliableField(ProductField.COST, minimumConfidence)) {
			return "The product cost column was not identified with sufficient confidence";
		}
		return "";
	}

	private boolean hasReliableField(ProductField field, double minimumConfidence) {
		return columnOf(field) >= 0
				&& confidences.getOrDefault(field, 0.0) >= minimumConfidence;
	}
}
