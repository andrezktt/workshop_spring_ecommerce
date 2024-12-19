package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.CategoryDTO;
import com.andrezktt.ecommerce.dto.ProductDTO;
import com.andrezktt.ecommerce.dto.ProductMinDTO;
import com.andrezktt.ecommerce.entities.Category;
import com.andrezktt.ecommerce.entities.Product;
import com.andrezktt.ecommerce.repositories.CategoryRepository;
import com.andrezktt.ecommerce.repositories.ProductRepository;
import com.andrezktt.ecommerce.services.exceptions.DatabaseException;
import com.andrezktt.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return repository.findAll().stream().map(CategoryDTO::new).toList();
    }
}
