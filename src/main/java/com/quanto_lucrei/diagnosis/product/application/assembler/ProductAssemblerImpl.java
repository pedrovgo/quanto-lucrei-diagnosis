package com.quanto_lucrei.diagnosis.product.application.assembler;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.quanto_lucrei.diagnosis.product.application.model.ProductColumnMapping;
import com.quanto_lucrei.diagnosis.product.application.model.ProductField;
import com.quanto_lucrei.diagnosis.product.domain.Product;

@Component
public class ProductAssemblerImpl implements ProductAssembler {

	private static final Pattern NON_DECIMAL_CHARACTERS = Pattern.compile("[^0-9,.-]");

	@Override
	public List<Product> assemble(
			List<List<String>> rows,
			ProductColumnMapping mapping
	) {
		return rows.stream()
				.skip(1)
				.map(row -> assembleRow(row, mapping))
				.filter(product -> product != null)
				.toList();
	}

	private Product assembleRow(List<String> row, ProductColumnMapping mapping) {
		String name = value(row, mapping.columnOf(ProductField.NAME));
		String code = value(row, mapping.columnOf(ProductField.CODE));
		if (name.isBlank() && code.isBlank()) {
			return null;
		}

		return new Product(
				name,
				code,
				value(row, mapping.columnOf(ProductField.DESCRIPTION)),
				parseCost(value(row, mapping.columnOf(ProductField.COST)))
		);
	}

	private String value(List<String> row, int column) {
		return column < 0 || column >= row.size() ? "" : row.get(column).trim();
	}

	private BigDecimal parseCost(String value) {
		String normalized = NON_DECIMAL_CHARACTERS.matcher(value).replaceAll("");
		if (normalized.contains(",") && normalized.contains(".")) {
			normalized = normalized.lastIndexOf(',') > normalized.lastIndexOf('.')
					? normalized.replace(".", "").replace(",", ".")
					: normalized.replace(",", "");
		} else if (normalized.contains(",")) {
			normalized = normalized.replace(",", ".");
		}
		try {
			return normalized.isBlank() ? BigDecimal.ZERO : new BigDecimal(normalized);
		} catch (NumberFormatException exception) {
			return BigDecimal.ZERO;
		}
	}
}
