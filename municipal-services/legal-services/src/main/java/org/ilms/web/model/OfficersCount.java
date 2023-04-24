package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OfficersCount {
    @JsonProperty("TOTAL")
    private Integer TOTAL = null;

    @JsonProperty("DEC")
    private Integer DEC = null;

    @JsonProperty("RO")
    private Integer RO = null;

    @JsonProperty("OICA")
    private Integer OICA = null;

    @JsonProperty("AO")
    private Integer AO = null;

    @JsonProperty("OIC")
    private Integer OIC = null;

}
