package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HearingResponse {

    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("totalCount")
    private Integer totalCount =null;

    @JsonProperty("HearingList")
    private List<Hearing> hearingList = null;

    @JsonProperty("workflow")
    private Workflow workflow = null;
}
