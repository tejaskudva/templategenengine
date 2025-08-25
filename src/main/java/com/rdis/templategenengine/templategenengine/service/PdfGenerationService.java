package com.rdis.templategenengine.templategenengine.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdis.templategenengine.templategenengine.configurations.DocumentConfigProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine;
    private final DocumentConfigService dConfigService;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper;

    public byte[] generatePdf(String documentName, String identifier) throws Exception {
        log.info("Starting PDF generation for document: {}", documentName);

        // Step 1: Read the HTML template
        // String htmlContent = readHtmlTemplate(documentName);

        // Step 1: Build the data map (from repo layer or manually for now)
        Map<String, Object> data = fetchDataForDocument(documentName, identifier);

        // mock
        //Map<String, Object> data = fetchMockData();

        // Later: fetch data from DB and populate this map dynamically

        // Step 2: For now, just replace basic placeholders
        // htmlContent = replacePlaceholders(htmlContent, documentName, identifier);

        String htmlContent = processTemplate(documentName, data);

        // Step 3: Convert HTML to PDF
        return convertHtmlToPdf(htmlContent);
    }

    private String processTemplate(String documentName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        String templatePath = documentName.toLowerCase() + "/template"; // Thymeleaf will add `.html`
        return templateEngine.process(templatePath, context);
    }

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

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchDataForDocument(String documentName, String identifier) throws Exception {
        // Get document config
        DocumentConfigProperties.DocumentConfig config = dConfigService.getConfig(documentName);

        // Call stored procedure (assumes JSON output in one column)
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("identifier", identifier);

        // Call stored procedure / query
        String json = jdbcTemplate.queryForObject(
                config.getProcedure(), // e.g., "EXEC get_invoice_mock_data :identifier"
                params,
                String.class);

        // Parse JSON into map
        Map<String, Object> data = mapper.readValue(json,
                new TypeReference<Map<String, Object>>() {
                });

        // Enrich with system fields
        Map<String, Object> header = (Map<String, Object>) data.get("header");
        header.put("currentDate", LocalDate.now());

        return data;
    }

    public Map<String, Object> fetchMockData() throws Exception {
        InputStream is = new ClassPathResource("invoice-mock.json").getInputStream();
        Map<String, Object> data = mapper.readValue(is, new TypeReference<Map<String, Object>>() {
        });
        return data;
    }

}