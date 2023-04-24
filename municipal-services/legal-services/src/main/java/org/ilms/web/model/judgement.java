package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ilms.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class judgement {


    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("orderType")
    private String orderType;

    @JsonProperty("orderDate")
    private Long orderDate;

    @JsonProperty("decisionStatus")
    private String decisionStatus;

    @JsonProperty("complianceDate")
    private Long complianceDate;

    @JsonProperty("revisedComplianceDate")
    private Long revisedComplianceDate;

    @JsonProperty("orderNoOverride")
    private String orderNoOverride;

    @JsonProperty("revisedComplainceReason")
    private String revisedComplainceReason;

    @JsonProperty("complianceStatus")
    private String complianceStatus;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
