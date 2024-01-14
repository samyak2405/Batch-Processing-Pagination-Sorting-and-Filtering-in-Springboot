package com.javahunter.BatchProcessing.mapper;

import com.javahunter.BatchProcessing.entity.Product;
import com.javahunter.BatchProcessing.payload.request.ProductRequest;
import com.javahunter.BatchProcessing.payload.request.ProductUpdateRequest;
import com.javahunter.BatchProcessing.payload.response.ProductResponse;

import java.util.UUID;

public class ProductMapper {

    public static Product mapToProduct(ProductRequest productRequest){
        return Product.builder()
                .productName(productRequest.getProductName())
                .productDescription(productRequest.getProductDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .productUUID(UUID.randomUUID())
                .build();
    }

    public static ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .productName(product.getProductName())
                .productUUID(product.getProductUUID())
                .productDescription(product.getProductDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    public static Product mapToProductUpdate(ProductUpdateRequest productUpdateRequest, Product product) {
        product.setProductDescription(productUpdateRequest.getProductDescription());
        product.setPrice(productUpdateRequest.getPrice());
        product.setQuantity(productUpdateRequest.getQuantity());
        return product;
    }
}
