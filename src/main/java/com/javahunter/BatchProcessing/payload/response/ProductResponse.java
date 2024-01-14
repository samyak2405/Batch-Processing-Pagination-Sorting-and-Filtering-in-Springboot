package com.javahunter.BatchProcessing.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductResponse {

    private String productName;

    private UUID productUUID;

    private String productDescription;

    private Integer price;

    private Integer quantity;
}
