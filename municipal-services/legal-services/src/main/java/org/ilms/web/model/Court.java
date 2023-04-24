package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ilms.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Court {

    @JsonProperty ("id")
    private String id ;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty ("courtName")
    private String courtName ;

    @JsonProperty ("district")
    private String district ;

    @JsonProperty ("state")
    private String state ;

    @JsonProperty ("division")
    private String division ;

    @JsonProperty ("status")
    private Status status ;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
