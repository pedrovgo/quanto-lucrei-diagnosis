package com.quanto_lucrei.diagnosis.product.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.quanto_lucrei.diagnosis.product.exception.InvalidProductFileException;

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

	@ExceptionHandler(MultipartException.class)
	public ProblemDetail handleInvalidMultipartRequest() {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				"The request must be a valid multipart/form-data request"
		);
		problem.setTitle("Invalid multipart request");
		problem.setProperty("field", "file");
		return problem;
	}
}
