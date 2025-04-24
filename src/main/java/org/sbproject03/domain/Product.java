package org.sbproject03.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString
public class Product {

  @Id @Column(length = 30)
  private String productId;

  @Column(length = 50, nullable = false)
  private String productName;

  @NotNull @Min(0) @Digits(integer = 9, fraction = 0)
  private int productPrice;

  @NotNull @Min(0)
  private int productStock;

  @NotBlank
  @Column(nullable = false, columnDefinition = "TEXT")
  private String productDescription;

  @NotBlank
  @Column(length = 15, nullable = false)
  private String productCategory;

  @Column(columnDefinition = "boolean default false")
  private boolean isBestItem = false;
}

