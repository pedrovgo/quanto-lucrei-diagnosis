package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class JevDecisionClient {

	private static final String DECISIONS_URL = "https://openrouter.ai/api/alpha/decisions";

	private final RestClient restClient;
	private final String apiKey;

	public JevDecisionClient(@Value("${openrouter.api-key:}") String apiKey) {
		this.restClient = RestClient.create();
		this.apiKey = apiKey;
	}

	public JevDecisionResponse classify(JevDecisionRequest request) {
		if (apiKey.isBlank()) {
			throw new IllegalStateException(
					"OpenRouter API key is not configured. Set OPENROUTER_API_KEY."
			);
		}

		return restClient.post()
				.uri(DECISIONS_URL)
				.header("Authorization", "Bearer " + apiKey)
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.body(JevDecisionResponse.class);
	}
}
