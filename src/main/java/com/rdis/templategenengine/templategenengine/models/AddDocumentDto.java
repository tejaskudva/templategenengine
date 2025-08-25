package com.rdis.templategenengine.templategenengine.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddDocumentDto {

    private String userDBId;
    private String cabinetName;
    @JsonProperty("NGOAddDocumentBDO")
    private NGOAddDocumentBDO ngoAddDocumentBdo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NGOAddDocumentBDO {

        private String volumeId;
        @Builder.Default
        private String accessType = "I";
        @Builder.Default
        private String encrFlag = "N";
        private String documentName;
        @Builder.Default
        private String createdByAppName = "pdf";
        private String comment;
        private String folderIndex;
    }
}
