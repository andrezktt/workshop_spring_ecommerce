package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.ProductDTO;
import com.andrezktt.ecommerce.entities.Product;
import com.andrezktt.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return new ProductDTO(repository.findById(id).orElseThrow());
    }
}
