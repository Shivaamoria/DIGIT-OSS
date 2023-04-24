package org.ilms.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Workflow {

    @JsonProperty("action")
    private String action = null;

    @JsonProperty("assignes")
    @Valid
    private List<String> assignes = null;

    @JsonProperty("comments")
    private String comments = null;


    @JsonProperty("rating")
    private Integer rating = null;
}

