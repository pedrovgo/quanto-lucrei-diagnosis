package com.quanto_lucrei.diagnosis.product.infrastructure.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.quanto_lucrei.diagnosis.product.application.port.out.ProductFileReader;
import com.quanto_lucrei.diagnosis.product.exception.InvalidProductFileException;

@Component
public class ProductTableReader implements ProductFileReader {

	private final DataFormatter dataFormatter = new DataFormatter(Locale.US);

	@Override
	public List<List<String>> read(MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename() == null
					? ""
					: file.getOriginalFilename().toLowerCase(Locale.ROOT);
			if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
				return readWorkbook(file);
			}
			if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
				return readDelimitedFile(file);
			}
			throw new InvalidProductFileException("Supported file formats are CSV, TXT, XLSX and XLS");
		} catch (IOException exception) {
			throw new InvalidProductFileException(
					"Could not read the product file: " + exception.getMessage()
			);
		}
	}

	private List<List<String>> readWorkbook(MultipartFile file) throws IOException {
		try (InputStream inputStream = file.getInputStream();
			 Workbook workbook = WorkbookFactory.create(inputStream)) {
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				List<List<String>> rows = new ArrayList<>();
				for (Row row : workbook.getSheetAt(sheetIndex)) {
					List<String> values = new ArrayList<>();
					for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
						Cell cell = row.getCell(cellIndex);
						values.add(cell == null ? "" : dataFormatter.formatCellValue(cell).trim());
					}
					if (values.stream().anyMatch(value -> !value.isBlank())) {
						rows.add(values);
					}
				}
				if (!rows.isEmpty()) {
					return rows;
				}
			}
			return List.of();
		}
	}

	private List<List<String>> readDelimitedFile(MultipartFile file) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String firstLine = reader.readLine();
			if (firstLine == null) {
				return List.of();
			}
			String content = firstLine + System.lineSeparator()
					+ reader.lines().collect(Collectors.joining(System.lineSeparator()));
			CSVFormat format = CSVFormat.DEFAULT.builder()
					.setDelimiter(delimiterOf(firstLine))
					.setTrim(true)
					.build();
			try (CSVParser parser = format.parse(new java.io.StringReader(content))) {
				List<List<String>> rows = new ArrayList<>();
				for (CSVRecord record : parser) {
					rows.add(record.stream().map(String::trim).toList());
				}
				return rows;
			}
		}
	}

	private char delimiterOf(String line) {
		return Arrays.stream(new Character[]{',', ';', '\t', '|'})
				.max((left, right) -> Long.compare(
						line.chars().filter(character -> character == left).count(),
						line.chars().filter(character -> character == right).count()
				))
				.orElse(',');
	}
}
