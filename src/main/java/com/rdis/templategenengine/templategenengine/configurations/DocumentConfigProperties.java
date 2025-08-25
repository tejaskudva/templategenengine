package com.rdis.templategenengine.templategenengine.configurations;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "documents")
@Data
public class DocumentConfigProperties {

    private Map<String, DocumentConfig> documents;

    @Data
    public static class DocumentConfig {
        private String procedure;
        private String template;
        private List<String> fields;
    }
}
