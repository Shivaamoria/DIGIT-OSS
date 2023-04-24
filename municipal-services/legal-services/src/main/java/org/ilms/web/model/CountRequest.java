package org.ilms.web.model;

import java.util.List;
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
public class CountRequest {
    @JsonProperty ("query")
    private String query;

    @JsonProperty ("preparedStatement")
    private List<Object> preparedStatement;
}
