package com.andrezktt.ecommerce.repositories;

import com.andrezktt.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
