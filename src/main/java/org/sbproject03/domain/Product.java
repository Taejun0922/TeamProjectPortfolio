package org.sbproject03.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @ToString
@Entity
public class Product {
  @Id
  @Column(columnDefinition = "varchar(30)")
  private String productId;

  @Column(columnDefinition = "varchar(50) not null")
  private String productName;

  @NotNull @Min(value = 0) @Digits(integer = 9, fraction = 0)
  private int productPrice;

  @NotNull @Min(value = 0)
  private int productStock;

  @Column(columnDefinition = "text")
  @NotBlank
  private String productDescription;

  @Column(columnDefinition = "varchar(15)")
  @NotBlank
  private String productCategory;

  /* 메인 상단 베스트 상품 */
  @Column(columnDefinition = "boolean default false")
  private boolean isBestItem = false;

}
