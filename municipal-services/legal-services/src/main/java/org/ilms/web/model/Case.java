package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.ilms.web.model.enums.CreationReason;
import org.ilms.web.model.enums.Status;
import org.ilms.web.model.workflow.ProcessInstance;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Case {
    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("number")
    private String number;

    @JsonProperty("cnrNumber")
    private String cnrNumber;

    @JsonProperty("parentCaseId")
    private String parentCaseId;


    @JsonProperty("linkedCases")
    private JsonNode linkedCases;

    @JsonProperty("type")
    private String type;

    @JsonProperty("category")
    private String category;

    @JsonProperty("filingNumber")
    private String filingNumber;

    @JsonProperty("filingDate")
    private Long filingDate;

    @JsonProperty("registrationDate")
    private Long registrationDate;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("arisingDetails")
    private String arisingDetails;

    @JsonProperty("applicationNo")
    private String applicationNumber;

    @JsonProperty("policyOrNonPolicyMatter")
    private String policyOrNonPolicyMatter;

    @JsonProperty("caseStatus")
    private String caseStatus;

    @JsonProperty("stage")
    private String stage;

    @JsonProperty("subStage")
    private String subStage;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("recommendOic")
    private String recommendOIC;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("parties")
    private List<Party> parties;


    @JsonProperty("act")
    private Act act;

    @JsonProperty("court")
    private Court court;

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("creationReason")
    @NotNull(message = "The value provided is either Invald or null")
    private CreationReason creationReason;

    @JsonProperty("workflow")
    @DiffIgnore
    private ProcessInstance workflow;

}
