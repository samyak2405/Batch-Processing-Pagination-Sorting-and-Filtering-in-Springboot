package com.javahunter.BatchProcessing.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Tag(name = "ProductRequest",description = "This for sending a product request")
public class ProductRequest {

    @NotNull(message = "Product name must be provided")
    @Size(min = 1)
    @Schema(example = "Product 1")
    private String productName;

    @Size(min = 5, max = 200)
    @Schema(example = "This is product 1 Description")
    private String productDescription;

    @Schema(example = "100")
    private Integer price;

    @Schema(example = "10")
    private Integer quantity;
}
