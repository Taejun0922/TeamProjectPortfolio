package org.sbproject03.repository;

import org.sbproject03.domain.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, String> {
    List<CartItems> findAllByCartRefId(String CartRefId);
    @Transactional
    void deleteByProductIdAndCartRefId(String productId, String cartId);
}
