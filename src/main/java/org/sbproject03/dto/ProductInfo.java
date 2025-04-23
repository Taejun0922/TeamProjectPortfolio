package org.sbproject03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductInfo {
  private String productId;
  private String productName;
  private int quantity;
  private int productPrice;
}
