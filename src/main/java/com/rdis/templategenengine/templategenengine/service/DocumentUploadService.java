package com.rdis.templategenengine.templategenengine.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdis.templategenengine.templategenengine.configurations.NewgenConfigurations;
import com.rdis.templategenengine.templategenengine.models.AddDocumentDto;
import com.rdis.templategenengine.templategenengine.models.AddDocumentDto.NGOAddDocumentBDO;
import com.rdis.templategenengine.templategenengine.repository.TemplateRepository;
import com.rdis.templategenengine.templategenengine.models.NGOAddDocumentResponseBDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class DocumentUploadService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TemplateRepository repo;
    private final NewgenConfigurations ngConfig;

    public void uploadDocumentToOD(String documentName, String folderIndex, byte[] documentResource) {

        try {
            String sessionId = repo.getSTypeUserSessionId();

            AddDocumentDto request = AddDocumentDto.builder()
                    .cabinetName(ngConfig.getCabinetName())
                    .userDBId(sessionId)
                    .ngoAddDocumentBdo(
                            NGOAddDocumentBDO.builder().documentName(documentName)
                                    .folderIndex(folderIndex).volumeId(ngConfig.getVolumeId()).build())
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(request);
            log.info("Final JSON text for file resource: {}", jsonRequest);

            // Prepare the file resource from byte array
            ByteArrayResource fileResource = new ByteArrayResource(documentResource) {
                @Override
                public String getFilename() {
                    return documentName + ".pdf";
                }
            };

            // Build the multipart request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("NGOAddDocumentBDO", jsonRequest);

            // Set the headers. Note that we do NOT set the Content-Type header.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Spring will add the boundary

            // Create the HttpEntity, combining headers and body
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Make the POST request
            ResponseEntity<NGOAddDocumentResponseBDO> response = restTemplate.exchange(
                    ngConfig.getOdBaseUrl() + "/OmniDocsRestWS/rest/services/addDocumentJSON",
                    HttpMethod.POST,
                    requestEntity,
                    NGOAddDocumentResponseBDO.class);

            log.info("Final API Response: {}, {}", response.getStatusCode(), response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("OD Rest Endpoint giving error: {}, {}",
                        response.getStatusCode(), response.getBody());
            }

        } catch (HttpClientErrorException e) {
            // Handle 4xx client errors
            log.error("Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

        } catch (HttpServerErrorException e) {
            // Handle 5xx server errors
            log.error("Server Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            // Handle network or I/O errors
            log.error("Network Error: ", e);

        } catch (RestClientException e) {
            // Catch-all for any other RestTemplate exceptions
            log.error("Rest Generic Error: ", e);

        } catch (Exception e) {
            log.error("Some error while uploading document: ", e);
        }
    }
}
