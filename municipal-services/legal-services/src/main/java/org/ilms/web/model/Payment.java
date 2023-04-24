package org.ilms.web.model;

import org.ilms.web.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("caseId")
    private String caseId;

    @JsonProperty ("hearingId")
    private String hearingId;

    @JsonProperty ("fineImposedDate")
    private Long fineImposedDate;

    @JsonProperty ("fineDueDate")
    private Long fineDueDate;

    @JsonProperty ("fineAmount")
    private String fineAmount;

    @JsonProperty ("status")
    private Status status;

    @JsonProperty ("auditDetails")
    private AuditDetails auditDetails;
}
