package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.CategoryDTO;
import com.andrezktt.ecommerce.entities.Category;
import com.andrezktt.ecommerce.repositories.CategoryRepository;
import com.andrezktt.ecommerce.tests.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Category category;
    private List<Category> list;

    @BeforeEach
    void setUp() throws Exception {
        category = CategoryFactory.createCategory();

        list = new ArrayList<>();
        list.add(category);

        Mockito.when(repository.findAll()).thenReturn(list);
    }

    @Test
    public void findAlShouldReturnAListOfCategoryDTO() {
        List<CategoryDTO> result = service.findAll();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(category.getId(), result.getFirst().getId());
        Assertions.assertEquals(category.getName(), result.getFirst().getName());
    }
}
