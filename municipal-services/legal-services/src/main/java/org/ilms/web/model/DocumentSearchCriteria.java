package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSearchCriteria {
    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("caseId")
    private List<String> caseId;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("caseNumber")
    private List<String> caseNumber;

    @JsonProperty("fileStoreId")
    private List<String> fileStoreId;

    @JsonProperty("cnrNumber")
    private List<String> cnrNumber;

    @JsonProperty("id")
    private List<String> id;

    @JsonProperty("sortBy")
    private SortBy sortBy;

    @JsonProperty("sortOrder")
    private SortOrder sortOrder;

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortBy {
        caseId,
        documentType
    }
}