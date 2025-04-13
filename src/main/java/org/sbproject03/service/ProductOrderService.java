package org.sbproject03.service;

import org.sbproject03.domain.ProductOrder;
import org.sbproject03.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOrderService {
  @Autowired
  private ProductOrderRepository productOrderRepository;

  public void save(ProductOrder productOrder) {
    productOrderRepository.save(productOrder);
  }
}
