package com.quanto_lucrei.diagnosis.product.application.model;

import java.util.Map;

public record ProductColumnMapping(
		Map<ProductField, Integer> columns,
		Map<ProductField, Double> confidences
) {

	public static final double MINIMUM_CONFIDENCE = 0.75;

	public int columnOf(ProductField field) {
		return columns.getOrDefault(field, -1);
	}

	public boolean isReliable() {
		return hasReliableField(ProductField.NAME)
				&& hasReliableField(ProductField.COST);
	}

	public String reliabilityReason() {
		if (!hasReliableField(ProductField.NAME)) {
			return "The product name column was not identified with sufficient confidence";
		}
		if (!hasReliableField(ProductField.COST)) {
			return "The product cost column was not identified with sufficient confidence";
		}
		return "";
	}

	private boolean hasReliableField(ProductField field) {
		return columnOf(field) >= 0
				&& confidences.getOrDefault(field, 0.0) >= MINIMUM_CONFIDENCE;
	}
}
