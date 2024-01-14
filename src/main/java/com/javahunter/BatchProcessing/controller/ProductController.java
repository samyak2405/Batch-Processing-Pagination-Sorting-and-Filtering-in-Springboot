package com.javahunter.BatchProcessing.controller;

import com.javahunter.BatchProcessing.payload.request.ProductRequest;
import com.javahunter.BatchProcessing.payload.request.ProductUpdateRequest;
import com.javahunter.BatchProcessing.payload.response.ApiResponses;
import com.javahunter.BatchProcessing.payload.response.ProductResponse;
import com.javahunter.BatchProcessing.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Page;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Product Controller",
        description = "This controller has all the endpoints needed for Product related CRUD operations")
public class ProductController {

    private final ProductService productService;

    /**
     * @Hidden: This annotation is used to hide any controller or method from swagger documenting.
     * This annotation can be applied at class level as well as method level
     */
    @Operation(
            summary = "Import Data from CSV file",
            description = "This method allows to import Product data from CSV file and stores it in Database " +
                    "using Batch Processing")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data imported successfully",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The job instance is already complete",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The job execution is already running",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "400",
                            description = "The provided job parameters are invalid",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The job cannot be restarted",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/importCsv")
    public ResponseEntity<ApiResponses> importCsvToDBJob() {
        log.info("Request hitting Import CSV to DB Job Controller");
        ApiResponses apiResponses = null;
        try {
            apiResponses = productService.importCsvToDB();
        } catch (JobInstanceAlreadyCompleteException e) {
            return new ResponseEntity<>(ApiResponses.builder()
                    .statusCode(HttpStatus.CONFLICT.toString())
                    .message("The job instance is already complete")
                    .build(), HttpStatus.CONFLICT);
        } catch (JobExecutionAlreadyRunningException e) {
            return new ResponseEntity<>(ApiResponses.builder()
                    .statusCode(HttpStatus.CONFLICT.toString())
                    .message("The job execution is already running")
                    .build(), HttpStatus.CONFLICT);
        } catch (JobParametersInvalidException e) {

            return new ResponseEntity<>(ApiResponses.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.toString())
                    .message("The provided job parameters are invalid")
                    .build(), HttpStatus.BAD_REQUEST);
        } catch (JobRestartException e) {
            return new ResponseEntity<>(ApiResponses.builder()
                    .statusCode(HttpStatus.CONFLICT.toString())
                    .message("The job cannot be restarted")
                    .build(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(apiResponses, HttpStatus.OK);
    }

    @Operation(
            summary = "Create Product",
            description = "This method allows to create a Product")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Product created successfully",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Product Already exist with provided name",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/create")
    public ResponseEntity<ApiResponses> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Request hitting Create Product Controller");
        ApiResponses apiResponses = productService.createProduct(productRequest);
        return new ResponseEntity<>(apiResponses, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update Product",
            description = "This method allows to update an existing product with name")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated Successfully",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product with name not found",
                            content = @Content(mediaType = "application/json"))
            })
    @PutMapping("/update")
    public ResponseEntity<ApiResponses> updateProduct(@Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        log.info("Request hitting Update Product Controller");
        ApiResponses apiResponses = productService.updateProduct(productUpdateRequest);
        return new ResponseEntity<>(apiResponses, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Product",
            description = "This method allows to delte an existing product with name")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product deleted Successfully",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product with name not found",
                            content = @Content(mediaType = "application/json"))
            })
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponses> deleteProduct(@RequestParam("productName") String productName) {
        log.info("Request hitting Delete product Controller");
        ApiResponses apiResponses = productService.deleteProduct(productName);
        return new ResponseEntity<>(apiResponses, HttpStatus.OK);
    }

    /**
     * Demo URL: http://localhost:9090/api/v1/product/sorted-product?sort=price,desc&sort=quantity,asc
     *
     * @param sort
     * @return
     */

    @Operation(
            summary = "Product in sorted form",
            description = "This method allows to get products in required sorted format. Implements sorting functionality.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of all products sorted in given order",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No product found",
                            content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/sorted-product")
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "productId,desc") String[] sort) {
        log.info("Request Hitting Get All products request");
        List<ProductResponse> productResponses = productService.getAllProducts(sort);
        if (productResponses.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Product per page",
            description = "This method allows to get product pagewise by taking parameters like page number," +
                    "page size and order in which each page will show the products.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Get All Products in page",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))}),
                    @ApiResponse(responseCode = "404",
                            description = "No product found",
                            content = @Content)})
    @GetMapping("/product-page")
    public ResponseEntity<Map<String, Object>> getAllProductPage(@ParameterObject
                                                                 @RequestParam(required = false) String productName,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "productId,desc") String[] sort
    ) {
        Map<String, Object> response = productService.findAllProducts(productName, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Product by Name",
            description = "This method allows to get product by name as name is unique")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found",
                            content = {
                                    @Content(mediaType = "application/json")
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product with name not found",
                            content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/get-by-name")
    public ResponseEntity<ProductResponse> getProductByName(@RequestParam(name = "productName") String productName) {
        ProductResponse productResponse = productService.getProductByName(productName);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

}
