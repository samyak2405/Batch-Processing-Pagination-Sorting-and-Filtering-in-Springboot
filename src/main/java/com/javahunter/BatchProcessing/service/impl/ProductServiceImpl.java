package com.javahunter.BatchProcessing.service.impl;

import com.javahunter.BatchProcessing.constants.ProductConstants;
import com.javahunter.BatchProcessing.entity.Product;
import com.javahunter.BatchProcessing.expection.custom.ProductAlreadyExistException;
import com.javahunter.BatchProcessing.expection.custom.ResourceNotFoundException;
import com.javahunter.BatchProcessing.mapper.ProductMapper;
import com.javahunter.BatchProcessing.payload.request.ProductRequest;
import com.javahunter.BatchProcessing.payload.request.ProductUpdateRequest;
import com.javahunter.BatchProcessing.payload.response.ApiResponses;
import com.javahunter.BatchProcessing.payload.response.ProductResponse;
import com.javahunter.BatchProcessing.repository.ProductRepository;
import com.javahunter.BatchProcessing.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final ProductRepository productRepository;
    @Override
    public ApiResponses importCsvToDB()
            throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt",System.currentTimeMillis())
                .toJobParameters();

            jobLauncher.run(job,jobParameters);

        return ApiResponses.builder()
                .message(ProductConstants.IMPORT_SUCCESS)
                .statusCode(HttpStatus.OK.toString())
                .build();
    }

    @Override
    public ApiResponses createProduct(ProductRequest productRequest) {
        log.info("Request for creating product");
        if (productRepository.existsByProductName(productRequest.getProductName())){
            log.info("Product name already exists: {}",productRequest.getProductName());
            throw new ProductAlreadyExistException(String.format(ProductConstants.PRODUCT_NAME_ALREADY_EXISTS,productRequest.getProductName()));
        }
        Product product = ProductMapper.mapToProduct(productRequest);
        Product savedProduct = productRepository.save(product);
        return ApiResponses.builder()
                .message(String.format("Product with name %s has been saved successfully",savedProduct.getProductName()))
                .statusCode(HttpStatus.CREATED.toString())
                .build();
    }

    @Override
    public ApiResponses updateProduct(ProductUpdateRequest productUpdateRequest) {
        log.info("Request for updating product");
        if(!productRepository.existsByProductName(productUpdateRequest.getProductName())){
            log.info("Product with name {} not found",productUpdateRequest.getProductName());
            throw new ResourceNotFoundException(String.format("%s %s",ProductConstants.RESOURCE_NOT_FOUND,productUpdateRequest.getProductName()));
        }
        Product product = productRepository.findByProductName(productUpdateRequest.getProductName());
        productRepository.save(ProductMapper.mapToProductUpdate(productUpdateRequest, product));
        return ApiResponses.builder()
                .message(ProductConstants.UPDATE_SUCCESSFUL)
                .statusCode(HttpStatus.OK.toString())
                .build();
    }

    @Override
    public ApiResponses deleteProduct(String productName) {
        log.info("Request for updating product");
        if(!productRepository.existsByProductName(productName)){
            log.info("Product with name {} not found",productName);
            throw new ResourceNotFoundException(String.format("%s %s",ProductConstants.RESOURCE_NOT_FOUND,productName));
        }
        productRepository.deleteByProductName(productName);
        return ApiResponses.builder()
                .statusCode(HttpStatus.OK.toString())
                .message(ProductConstants.DELETE_SUCCESSFUL)
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts(String[] sort) {
        List<Sort.Order> orders = getOrders(sort);
        List<Product> products = productRepository.findAll(Sort.by(orders));
        List<ProductResponse> productResponses = new ArrayList<>();
        products.forEach(product -> productResponses.add(ProductMapper.mapToProductResponse(product)));
        return productResponses;
    }

    private List<Sort.Order> getOrders(String[] sort) {
        for (String sortData: sort){
            log.info("{}",sortData);
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")){
            for (String sortOrder: sort){
                String[] filterSort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(filterSort[1]),filterSort[0]));
            }
        }else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

    @Override
    public ProductResponse getProductByName(String productName) {
        if(!productRepository.existsByProductName(productName)){
            log.info("Product with name {} not found",productName);
            throw new ResourceNotFoundException(String.format("%s %s",ProductConstants.RESOURCE_NOT_FOUND,productName));
        }
        Product product = productRepository.findByProductName(productName);
        return ProductMapper.mapToProductResponse(product);

    }

    @Override
    public Map<String,Object> findAllProducts(String productName,int page,int size,String[] sort) {
        List<Sort.Order> orders = getOrders(sort);
        Pageable pageable = PageRequest.of(page,size,Sort.by(orders));
        Page<Product> pageProducts;
        if (productName ==null || productName.isEmpty() || productName.isBlank())
            pageProducts = productRepository.findAll(pageable);
        else
            pageProducts = findByProductNameContaining(productName,pageable);
        return getResponseMapObject(pageProducts);
    }

    private static Map<String, Object> getResponseMapObject(Page<Product> pageProducts) {
        List<Product> products = pageProducts.getContent();
        if(products.isEmpty())
            throw new ResourceNotFoundException("No product found");
        List<ProductResponse> productResponses = new ArrayList<>();
        products.forEach(product -> productResponses.add(ProductMapper.mapToProductResponse(product)));
        Map<String,Object> response = new HashMap<>();
        response.put("Products",productResponses);
        response.put("currentPage", pageProducts.getNumber());
        response.put("totalItems", pageProducts.getTotalElements());
        response.put("totalPages", pageProducts.getTotalPages());
        return response;
    }

    @Override
    public Page<Product> findByProductNameContaining(String productName, Pageable pageable) {
        return productRepository.findByProductNameContaining(productName,pageable);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")){
            return Sort.Direction.ASC;
        }else if(direction.equals("desc")){
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

}
