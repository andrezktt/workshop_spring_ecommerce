package com.andrezktt.ecommerce.controllers.it;

import com.andrezktt.ecommerce.dto.ProductDTO;
import com.andrezktt.ecommerce.entities.Category;
import com.andrezktt.ecommerce.entities.Product;
import com.andrezktt.ecommerce.tests.CategoryFactory;
import com.andrezktt.ecommerce.tests.ProductFactory;
import com.andrezktt.ecommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminUsername, adminPassword;
    private String clientUsername, clientPassword;
    private String adminToken, clientToken, invalidToken;

    private String productName;
    private Product product;
    private ProductDTO productDTO;

    private Long existingProductId, nonExistingProductId, dependentProductId;

    @BeforeEach
    void setUp() throws Exception {
        existingProductId = 2L;
        nonExistingProductId = 100L;
        dependentProductId = 3L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto";

        productName = "Macbook";

        Category category = CategoryFactory.createCategory();
        product = ProductFactory.createProduct();
        product.getCategories().add(category);
        productDTO = new ProductDTO(product);
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products?name={productName}", productName)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products?name=")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(jsonPath("$.content[0].price").value(90.5));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingProductId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("Smart TV"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        result.andExpect(jsonPath("$.price").value(2190.0));
        result.andExpect(jsonPath("$.categories").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingProductId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDTOCreatedWhenLoggedAsAdmin() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(26L));
        result.andExpect(jsonPath("$.name").value("Playstation 5"));
        result.andExpect(jsonPath("$.description").value("The best console ever"));
        result.andExpect(jsonPath("$.price").value(5000.0));
        result.andExpect(jsonPath("$.imgUrl").value("https://img.com/img.png"));
        result.andExpect(jsonPath("$.categories[0].id").value(1L));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndInvalidName() throws Exception{

        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndInvalidDescription() throws Exception{

        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndPriceIsNegative() throws Exception{

        product.setPrice(-50.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndPriceIsZero() throws Exception{

        product.setPrice(0.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminProductHasNoCategory() throws Exception{

        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnForbiddenWhenLoggedAsClient() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExistsAndAdminLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("Playstation 5"));
        result.andExpect(jsonPath("$.description").value("The best console ever"));
        result.andExpect(jsonPath("$.price").value(5000.0));
        result.andExpect(jsonPath("$.imgUrl").value("https://img.com/img.png"));
        result.andExpect(jsonPath("$.categories[0].id").value(1L));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName() throws Exception {

        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidDescription() throws Exception {

        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsNegative() throws Exception {

        product.setPrice(-2.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndPriceIsZero() throws Exception {

        product.setPrice(0.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndProductHasNoCategory() throws Exception {

        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnForbiddenWhenIdExistsAndClientLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndLoggedAsAdmin() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistsAndLoggedAsAdmin() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBadRequestWhenIdIsDependentAndLoggedAsAdmin() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentProductId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void deleteShouldReturnForbiddenWhenIdExistsAndLoggedAsClient() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnForbiddenWhenIdDoesExistAndLoggedAsClient() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingProductId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
}
