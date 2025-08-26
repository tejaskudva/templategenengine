package com.rdis.templategenengine.templategenengine.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ODDocumentUploadResponseDto {

    @JsonProperty("NGOAddDocumentResponseBDO")
    private NGOAddDocumentResponseBDO ngoAddDocumentResponseBDO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NGOAddDocumentResponseBDO {
        private String status;
        private String message;
        @JsonProperty("NGOGetDocListDocDataBDO")
        private NGOGetDocListDocDataBDO NGOGetDocListDocDataBDO;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NGOGetDocListDocDataBDO {
            private String accessDateTime;
            private String author;
            private String checkOutBy;
            private String checkoutStatus;
            private String comment;
            private String createdByApp;
            private String createdByAppName;
            private String createdDateTime;
            private String docOrderNo;
            private String documentIndex;
            private String documentLock;
            private String documentName;
            private String documentSize;
            private String documentType;
            private String documentVersionNo;
            private String enableLog;
            private String expiryDateTime;
            private String filedByUser;
            private String filedDateTime;
            private String finalizedBy;
            private String FTSDocumentIndex;
            private String iSIndex;
            private String location;
            private String lockByUser;
            private String loginUserRights;
            private String noOfPages;
            private String owner;
            private String ownerIndex;
            private String parentFolderIndex;
            private String referenceFlag;
            private String revisedDateTime;
            private String textISIndex;
            private String useFulInfo;
            private String versionFlag;
            @JsonProperty("NGOGlobalIndexBDO")
            private String NGOGlobalIndexBDO;
        }
    }
}