package com.javahunter.BatchProcessing.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Tag(name = "ProductUpdateRequest",description = "This is for updating a product")
public class ProductUpdateRequest {

    @Schema(example = "Product 1")
    private String productName;

    @Size(min = 5, max = 200)
    @Schema(example = "This is Product 1 Description")
    private String productDescription;

    @Schema(example = "100")
    private Integer price;

    @Schema(example = "10")
    private Integer quantity;
}
