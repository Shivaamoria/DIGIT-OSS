package org.ilms.web.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ilms.web.model.OwnerInfo;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Setter
public class CreateUserRequest {

    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("user")
    private OwnerInfo user;

    
}


