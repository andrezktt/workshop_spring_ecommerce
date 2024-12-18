package com.andrezktt.ecommerce.repositories;

import com.andrezktt.ecommerce.entities.OrderItem;
import com.andrezktt.ecommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
