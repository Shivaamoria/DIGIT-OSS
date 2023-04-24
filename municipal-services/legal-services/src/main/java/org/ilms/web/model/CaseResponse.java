package org.ilms.web.model;

import java.util.HashMap;
import java.util.List;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CaseResponse {
    @JsonProperty ("responseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty ("officersCount")
    private OfficersCount officersCount = null;

    @JsonProperty ("totalCount")
    private Integer totalCount = null;

    @JsonProperty("statusMap")
    private List<HashMap<String,Object>> statusMap = null;

    @JsonProperty ("caseList")
    private List<Case> caseList = null;
    
}
