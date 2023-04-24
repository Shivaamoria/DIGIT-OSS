package org.ilms.web.model;

import org.egov.common.contract.request.RequestInfo;
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
public class CaseRequest {
    @JsonProperty ("RequestInfo")
    private RequestInfo RequestInfo;

    @JsonProperty ("case")
    private Case caseObj;

}
