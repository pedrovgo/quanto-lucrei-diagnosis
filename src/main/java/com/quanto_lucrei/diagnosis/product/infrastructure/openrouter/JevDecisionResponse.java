package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JevDecisionResponse(
		String model,
		Map<String, JevAnswer> answers,
		JevUsage usage,
		String id,
		String provider
) {
	public record JevAnswer(
			String type,
			String choice,
			Map<String, Double> probabilities,
			double confidence
	) {
	}

	public record JevUsage(
			@JsonProperty("input_tokens")
			int inputTokens,
			@JsonProperty("output_tokens")
			int outputTokens,
			double cost
	) {
	}
}
