package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.ilms.web.model.enums.Status;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Hearing {
    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("hearingNumber")
    private String hearingNumber;

    @JsonProperty("courtNumber")
    private String courtNumber;

    @JsonProperty("bench")
    private String bench;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("judgeName")
    private JsonNode judgeName;

    @JsonProperty("hearingDate")
    private Long hearingDate;

    @JsonProperty("businessDate")
    private Long businessDate;

    @JsonProperty("hearingPurpose")
    private String hearingPurpose;

    @JsonProperty("requiredOfficer")
    private String requiredOfficer;

    @JsonProperty("affidavitFilingDate")
    private Long affidavitFilingDate;

    @JsonProperty("affidavitFilingDueDate")
    private Long affidavitFilingDueDate;

    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("oathNumber")
    private String oathNumber;

    @JsonProperty("nextHearingDate")
    private Long nextHearingDate;

    @JsonProperty("isPresenceRequired")
    private Boolean isPresenceRequired;

    @JsonProperty("hearingType")
    private String hearingType;

    @JsonProperty("departmentOfficer")
    private String departmentOfficer;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("status")
    private Status status;

    //    @JsonProperty("petitioner")
//    private Party petitioner;
//
//    @JsonProperty("respondent")
//    private Party respondent;
    @JsonProperty("parties")
    private List<Party> parties;

    @JsonProperty("payment")
    private Payment payment;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
