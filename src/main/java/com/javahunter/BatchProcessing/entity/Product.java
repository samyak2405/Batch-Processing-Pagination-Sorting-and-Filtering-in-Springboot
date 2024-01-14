package com.javahunter.BatchProcessing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_generator")
    @SequenceGenerator(
            name = "product_generator",
            sequenceName = "product_sequence",
            allocationSize = 1)
    private Integer productId;

    private UUID productUUID;

    @Column(unique = true)
    private String productName;

    private String productDescription;

    private Integer price;

    private Integer quantity;
}
