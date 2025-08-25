package com.rdis.templategenengine.templategenengine.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdis.templategenengine.templategenengine.configurations.DocumentConfigProperties;
import com.rdis.templategenengine.templategenengine.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine;
    private final DocumentConfigService dConfigService;
    private final TemplateRepository repo;
    private final ObjectMapper mapper;

    // @formatter:off
    /**
     * Generates a PDF document from a Thymeleaf template and data.
     * This is the main orchestrator method that combines all steps:
     * 1. Fetches data from a database or uses mock data.
     * 2. Processes a Thymeleaf template to create HTML content.
     * 3. Converts the HTML content into a byte array representing a PDF.
     *
     * @param documentName The name of the document, which corresponds to the template and mock data file names.
     * @param identifier The unique identifier used to fetch live data from the database.
     * @param mock A flag to determine whether to use mock data (true) or live data from the database (false).
     * @return A byte array containing the generated PDF document.
     * @throws Exception if any part of the PDF generation process fails (e.g., data fetching, template processing, or conversion).
     */
    // @formatter:on
    public byte[] generatePdf(String documentName, String identifier, boolean mock) throws Exception {
        log.info("Starting PDF generation for document: {}", documentName);

        // Step 1: Build the data map (from repo layer or manually for now)
        Map<String, Object> data = new HashMap<>();
        if (mock) {
            data = fetchMockData(documentName);
        } else {
            data = fetchDataForDocument(documentName, identifier);
        }

        // Step 2: Build the HTML using ThymeLeaf
        String htmlContent = processTemplate(documentName, data);

        // Step 3: Convert HTML to PDF
        return convertHtmlToPdf(htmlContent);
    }

    // @formatter:off
    /**
     * Processes a Thymeleaf template with the provided data to generate HTML content.
     * The method automatically appends "/template" to the document name to locate the template file.
     *
     * @param documentName The base name of the document, used to find the corresponding Thymeleaf template.
     * @param data A map of key-value pairs to be injected into the template as variables.
     * @return A string containing the processed HTML content.
     */
    // @formatter:on
    private String processTemplate(String documentName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        String templatePath = documentName.toLowerCase() + "/template"; // Thymeleaf will add `.html`
        return templateEngine.process(templatePath, context);
    }

    // @formatter:off
    /**
     * Converts a string of HTML content into a PDF document as a byte array.
     * It uses the ITextRenderer library to perform the conversion.
     *
     * @param htmlContent The HTML content string to be converted to PDF.
     * @return A byte array containing the generated PDF document.
     * @throws Exception if the conversion process from HTML to PDF fails.
     */
    // @formatter:on
    private byte[] convertHtmlToPdf(String htmlContent) throws Exception {
        log.info("Converting HTML to PDF");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            // Set the HTML content
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("Successfully generated PDF, size: {} bytes", pdfBytes.length);

            return pdfBytes;
        }
    }

    // @formatter:off
    /**
     * Fetches live data for a document from a database using a stored procedure.
     * The method retrieves the stored procedure name from a document configuration service,
     * calls the repository layer to execute it, and then parses the JSON output into a map.
     * It also enriches the data with system fields, such as the current date.
     *
     * @param documentName The name of the document, used to look up the stored procedure.
     * @param identifier The unique identifier used as a parameter for the stored procedure.
     * @return A map containing the fetched and enriched data from the database.
     * @throws Exception if there is an issue with fetching data, executing the stored procedure, or parsing the JSON.
     */
    // @formatter:on
    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchDataForDocument(String documentName, String identifier) throws Exception {
        // Get document config
        DocumentConfigProperties.DocumentConfig config = dConfigService.getConfig(documentName);

        // Call stored procedure (assumes JSON output in one column)
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("identifier", identifier);

        // Call stored procedure / query
        String json = repo.fetchDocumentDataFromProc(identifier, config.getProcedure());

        // Parse JSON into map
        Map<String, Object> data = mapper.readValue(json,
                new TypeReference<Map<String, Object>>() {
                });

        // Enrich with system fields
        Map<String, Object> header = (Map<String, Object>) data.get("header");
        header.put("currentDate", LocalDate.now());

        return data;
    }

    // @formatter:off
    /**
     * Fetches mock data for a document from a JSON file located in the classpath.
     * The method assumes the mock JSON file is named `[documentName]-mock.json`.
     *
     * @param documentName The name of the document, used to construct the mock JSON file name.
     * @return A map containing the mock data parsed from the JSON file.
     * @throws Exception if the mock JSON file is not found or cannot be parsed.
     */
    // @formatter:on
    private Map<String, Object> fetchMockData(String documentName) throws Exception {
        InputStream is = new ClassPathResource(documentName + "-mock.json").getInputStream();
        Map<String, Object> data = mapper.readValue(is, new TypeReference<Map<String, Object>>() {
        });
        return data;
    }

}