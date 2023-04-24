package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ilms.web.model.enums.Status;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Document {

    @JsonProperty ("id")
    private String id ;

    @JsonProperty ("caseId")
    private String caseId ;

    @JsonProperty ("documentType")
    private String documentType ;

    @JsonProperty ("documentId")
    private String documentId ;

    @JsonProperty ("fileStoreId")
    private String fileStoreId ;

    @JsonProperty ("status")
    private Status status ;

    @JsonProperty ("remarks")
    private String remarks ;

    @JsonProperty ("auditDetails")
    private AuditDetails auditDetails ;

}
