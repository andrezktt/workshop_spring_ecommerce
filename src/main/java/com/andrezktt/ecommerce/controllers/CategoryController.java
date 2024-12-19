package com.andrezktt.ecommerce.controllers;

import com.andrezktt.ecommerce.dto.CategoryDTO;
import com.andrezktt.ecommerce.dto.ProductDTO;
import com.andrezktt.ecommerce.dto.ProductMinDTO;
import com.andrezktt.ecommerce.services.CategoryService;
import com.andrezktt.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
