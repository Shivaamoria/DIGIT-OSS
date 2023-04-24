package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ilms.web.model.enums.Status;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Advocate {

    @JsonProperty("id")
    private String id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
