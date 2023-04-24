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
public class Act {

    @JsonProperty ("id")
    private String id ;

    @JsonProperty ("caseId")
    private String caseId ;

    @JsonProperty("actName")
    private String actName ;

    @JsonProperty("sectionNumber")
    private String sectionNumber ;

    @JsonProperty("status")
    private Status status ;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails ;
}
