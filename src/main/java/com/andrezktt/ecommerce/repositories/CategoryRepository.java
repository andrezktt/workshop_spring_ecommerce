package com.andrezktt.ecommerce.repositories;

import com.andrezktt.ecommerce.entities.Category;
import com.andrezktt.ecommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
