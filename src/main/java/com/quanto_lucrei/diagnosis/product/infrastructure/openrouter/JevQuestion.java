package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import java.util.Map;

public record JevQuestion(
		String type,
		String instructions,
		Map<String, String> criteria
) {
}
