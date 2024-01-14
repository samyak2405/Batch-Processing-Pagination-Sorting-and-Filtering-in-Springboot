package com.javahunter.BatchProcessing.expection;

import com.javahunter.BatchProcessing.constants.ProductConstants;
import com.javahunter.BatchProcessing.expection.custom.ProductAlreadyExistException;
import com.javahunter.BatchProcessing.expection.custom.ResourceNotFoundException;
import com.javahunter.BatchProcessing.payload.response.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProductAlreadyExistException.class)
    public ResponseEntity<ApiResponses> handleProductAlreadyExistException(ProductAlreadyExistException ex) {

        return new ResponseEntity<>(ApiResponses.builder()
                .statusCode(HttpStatus.CONFLICT.toString())
                .message(ex.getMessage())
                .build(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponses> handleResourceNotFoundException(ResourceNotFoundException ex) {

        return new ResponseEntity<>(ApiResponses.builder()
                .statusCode(HttpStatus.NOT_FOUND.toString())
                .message(String.format(ProductConstants.RESOURCE_NOT_FOUND))
                .build(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        return new ResponseEntity<>(ApiResponses.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.toString())
                .build()
                , HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = null;
        if (Objects.requireNonNull(ex.getRequiredType()).isEnum()) {
            var enumClass = (Class<? extends Enum<?>>) ex.getRequiredType();
            String validEnumValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            message = String.format(ProductConstants.TYPE_MISMATCH,ex.getValue(), validEnumValues);
        } else {
            message =  String.format(ProductConstants.TYPE_MISMATCH, ex.getValue(), ex.getRequiredType().isEnum());
        }

        return new ResponseEntity<>(ApiResponses.builder()
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.toString())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errorResponse.put(fieldName, message);
                });

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
