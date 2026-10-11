package com.quanto_lucrei.diagnosis.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductImportExceptionHandler {

	@ExceptionHandler(InvalidProductFileException.class)
	public ProblemDetail handleInvalidProductFile(InvalidProductFileException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				exception.getMessage()
		);
		problem.setTitle("Invalid product file");
		problem.setProperty("field", "file");
		return problem;
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ProblemDetail handleMissingFile(MissingServletRequestPartException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				"The multipart field 'file' is required"
		);
		problem.setTitle("Missing product file");
		problem.setProperty("field", exception.getRequestPartName());
		return problem;
	}
}
