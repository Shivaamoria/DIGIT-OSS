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
public class CaseDetailsResponse {
    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("totalCount")
    private Integer totalCount = null;

    @JsonProperty("caseList")
    private List<Case> caseList = null;

    @JsonProperty("HearingList")
    private List<Hearing> HearingList = null;

    @JsonProperty("judgementList")
    private List<judgement> judgementList = null;

    @JsonProperty("workflow")
    private Workflow workflow = null;

    public CaseDetailsResponse responseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }

}