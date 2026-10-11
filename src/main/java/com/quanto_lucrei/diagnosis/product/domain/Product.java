package com.quanto_lucrei.diagnosis.product.domain;

import java.math.BigDecimal;

public record Product(
		String name,
		String code,
		String description,
		BigDecimal cost
) {
}
