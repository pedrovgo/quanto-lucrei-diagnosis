package com.quanto_lucrei.diagnosis.product.infrastructure.openrouter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JevDecisionRequestFactory {

	private final String model;

	public JevDecisionRequestFactory(
			@Value("${openrouter.model:typesafe/jev-1.13}") String model
	) {
		this.model = model;
	}

	public JevDecisionRequest create(List<List<String>> rows) {
		if (rows.isEmpty()) {
			throw new IllegalArgumentException("The uploaded file has no tabular data");
		}

		return new JevDecisionRequest(model, buildState(rows), buildQuestions(rows));
	}

	private Map<String, JevColumnState> buildState(List<List<String>> rows) {
		int columnCount = rows.stream().mapToInt(List::size).max().orElse(0);
		Map<String, JevColumnState> state = new LinkedHashMap<>();
		for (int column = 0; column < columnCount; column++) {
			final int columnIndex = column;
			String key = "col_" + (column + 1);
			String header = value(rows.getFirst(), columnIndex);
			List<String> values = rows.stream()
					.skip(1)
					.map(row -> value(row, columnIndex))
					.toList();
			state.put(key, new JevColumnState(header, values));
		}
		return state;
	}

	private Map<String, JevQuestion> buildQuestions(List<List<String>> rows) {
		int columnCount = rows.stream().mapToInt(List::size).max().orElse(0);
		Map<String, JevQuestion> questions = new LinkedHashMap<>();
		for (int column = 0; column < columnCount; column++) {
			String key = "col_" + (column + 1);
			questions.put(key, new JevQuestion(
					"choice",
					"Classifique a coluna " + key
							+ ". Considere seu cabecalho e todos os valores fornecidos.",
					classificationCriteria()
			));
		}
		return questions;
	}

	private Map<String, String> classificationCriteria() {
		Map<String, String> criteria = new LinkedHashMap<>();
		criteria.put("nome", "Nome comercial ou titulo curto do produto.");
		criteria.put("descricao", "Texto com caracteristicas ou especificacoes do produto.");
		criteria.put("custo", "Custo monetario de aquisicao do produto.");
		criteria.put("codigo", "SKU, EAN, referencia ou identificador do produto.");
		criteria.put("outro", "Qualquer outra informacao ou coluna que nao possa ser classificada.");
		return criteria;
	}

	private String value(List<String> row, int column) {
		return column < row.size() ? row.get(column) : "";
	}
}
