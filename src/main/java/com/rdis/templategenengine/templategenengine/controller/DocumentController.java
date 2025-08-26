package com.rdis.templategenengine.templategenengine.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rdis.templategenengine.templategenengine.models.FinalMockDto;
import com.rdis.templategenengine.templategenengine.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PdfGenerationService pdfGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<Object> generateDocument(
            @RequestParam String documentName,
            @RequestParam String identifier,
            @RequestParam(defaultValue = "false") boolean mock,
            @RequestParam(defaultValue = "true") boolean returnPdf) {

        try {
            log.info("Generating document: {} with identifier: {}", documentName, identifier);

            byte[] pdfBytes = pdfGenerationService.generatePdf(documentName, identifier, mock);

            if (returnPdf) {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment",
                        documentName + "_" + identifier + ".pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(pdfBytes);
            } else {
                return ResponseEntity.ok()
                        .body(FinalMockDto.builder().response("Success").build());
            }

        } catch (Exception e) {
            log.error("Error generating document: {} for identifier: {}",
                    documentName, identifier, e);
            return ResponseEntity.internalServerError()
                    .body(FinalMockDto.builder().response("Failure").build());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Document generation service is running!");
    }
}