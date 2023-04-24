package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ilms.web.model.workflow.ProcessInstanceSearchCriteria;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestInfoWrapper {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;


    @JsonProperty("processSearchCriteria")
    private ProcessInstanceSearchCriteria processSearchCriteria;


    @JsonProperty ("isLoggedInAs")
    private String isLoggedInAs;


}
