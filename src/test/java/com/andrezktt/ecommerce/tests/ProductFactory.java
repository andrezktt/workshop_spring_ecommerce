package com.andrezktt.ecommerce.tests;

import com.andrezktt.ecommerce.entities.Category;
import com.andrezktt.ecommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct() {
        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L, "Playstation 5", "The best console ever", 5000.0, "https://img.com/img.png");
        product.getCategories().add(category);
        return product;
    }

    public static Product createProduct(String productName) {
        Product product = createProduct();
        product.setName(productName);
        return product;
    }
}
