package com.rdis.templategenengine.templategenengine.service;

import org.springframework.stereotype.Service;
import com.rdis.templategenengine.templategenengine.configurations.DocumentConfigProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentConfigService {

    private final DocumentConfigProperties documentConfigProperties;

    public DocumentConfigProperties.DocumentConfig getConfig(String documentName) {
        return documentConfigProperties.getMap().get(documentName.toLowerCase());
    }
}