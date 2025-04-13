package org.sbproject03.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @ToString @NoArgsConstructor
public class ProductInfo {
  private String productId;
  private String productName;
  private int quantity;
  private int productPrice;
}
