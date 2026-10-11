package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.application.model.ProductField;
import com.quanto_lucrei.diagnosis.product.application.port.out.ProductClassifier;

@Service
public class JevProductClassifier implements ProductClassifier {

	private final JevDecisionRequestFactory requestFactory;
	private final JevDecisionClient decisionClient;

	public JevProductClassifier(
			JevDecisionRequestFactory requestFactory,
			JevDecisionClient decisionClient
	) {
		this.requestFactory = requestFactory;
		this.decisionClient = decisionClient;
	}

	@Override
	public ProductColumnMapping classify(List<List<String>> rows) {
		JevDecisionResponse response = decisionClient.classify(requestFactory.create(rows));
		return toColumnMapping(response);
	}

	private ProductColumnMapping toColumnMapping(JevDecisionResponse response) {
		Map<ProductField, Integer> columns = new EnumMap<>(ProductField.class);
		Map<ProductField, Double> confidences = new EnumMap<>(ProductField.class);
		response.answers().forEach((columnName, answer) -> {
			ProductField field = toProductField(answer.choice());
			if (field != null
					&& answer.confidence() > confidences.getOrDefault(field, -1.0)) {
				columns.put(field, columnIndex(columnName));
				confidences.put(field, answer.confidence());
			}
		});
		return new ProductColumnMapping(columns, confidences);
	}

	private ProductField toProductField(String choice) {
		return switch (choice) {
			case "nome" -> ProductField.NAME;
			case "codigo" -> ProductField.CODE;
			case "descricao" -> ProductField.DESCRIPTION;
			case "custo" -> ProductField.COST;
			default -> null;
		};
	}

	private int columnIndex(String columnName) {
		return Integer.parseInt(columnName.substring("col_".length())) - 1;
	}
}
