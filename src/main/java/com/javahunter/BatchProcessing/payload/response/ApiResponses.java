package com.javahunter.BatchProcessing.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponses {
    @Schema(example = "200")
    private String statusCode;

    @Schema(example = "Api Response Message")
    private String message;
}
