package com.javahunter.BatchProcessing.service;

import com.javahunter.BatchProcessing.entity.Product;
import com.javahunter.BatchProcessing.payload.request.ProductRequest;
import com.javahunter.BatchProcessing.payload.request.ProductUpdateRequest;
import com.javahunter.BatchProcessing.payload.response.ApiResponses;
import com.javahunter.BatchProcessing.payload.response.ProductResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ApiResponses importCsvToDB() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

    ApiResponses createProduct(ProductRequest productRequest);

    ApiResponses updateProduct(ProductUpdateRequest productUpdateRequest);

    ApiResponses deleteProduct(String productName);

    List<ProductResponse> getAllProducts(String[] sort);

    ProductResponse getProductByName(String productName);

    Map<String,Object> findAllProducts(String productName, int page, int size, String[] sort);

    Page<Product> findByProductNameContaining(String productName, Pageable pageable);
}
