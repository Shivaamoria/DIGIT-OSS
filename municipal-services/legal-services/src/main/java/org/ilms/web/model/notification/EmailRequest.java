package org.ilms.web.model.notification;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailRequest {
    private RequestInfo requestInfo;

    private Email email;
}