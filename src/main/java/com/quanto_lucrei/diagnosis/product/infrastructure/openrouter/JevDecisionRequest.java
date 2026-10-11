package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import java.util.Map;

public record JevDecisionRequest(
		String model,
		Map<String, JevColumnState> state,
		Map<String, JevQuestion> questions
) {
}
