package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ilms.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Party {
    @JsonProperty("id")
    private String id;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("advocateId")
    private String advocateId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("petitionerType")
    private String petitionerType;

    @JsonProperty("address")
    private String address;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("partyType")
    private String partyType;

    @JsonProperty("advocate")
    private Advocate advocate;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
