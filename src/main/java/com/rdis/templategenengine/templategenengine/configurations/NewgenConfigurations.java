package com.rdis.templategenengine.templategenengine.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "newgen")
@Data
public class NewgenConfigurations {
    private String cabinetName;
    private String volumeId;
    private String fallbackFolderIndex;
    private String odBaseUrl;
}
