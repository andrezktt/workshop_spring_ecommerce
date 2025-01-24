package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.OrderDTO;
import com.andrezktt.ecommerce.entities.Order;
import com.andrezktt.ecommerce.entities.OrderItem;
import com.andrezktt.ecommerce.entities.Product;
import com.andrezktt.ecommerce.entities.User;
import com.andrezktt.ecommerce.repositories.OrderItemRepository;
import com.andrezktt.ecommerce.repositories.OrderRepository;
import com.andrezktt.ecommerce.repositories.ProductRepository;
import com.andrezktt.ecommerce.services.exceptions.ForbiddenException;
import com.andrezktt.ecommerce.services.exceptions.ResourceNotFoundException;
import com.andrezktt.ecommerce.tests.OrderFactory;
import com.andrezktt.ecommerce.tests.ProductFactory;
import com.andrezktt.ecommerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private Long existingOrderId;
    private Long nonExistingOrderId;
    private Long existingProductId;
    private Long nonExistingProductId;
    private Product product;
    private Order order;
    private OrderDTO orderDTO;
    private User admin;
    private User client;

    @BeforeEach
    void setUp() throws Exception {
        existingOrderId = 1L;
        nonExistingOrderId = 2L;

        existingProductId = 1L;
        nonExistingProductId = 2L;

        product = ProductFactory.createProduct();

        admin = UserFactory.createCustomAdminUser(1L, "jeff@gmail.com");
        client = UserFactory.createCustomClientUser(2L, "bob@gmail.com");

        order = OrderFactory.createOrder(client);
        orderDTO = new OrderDTO(order);

        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(any())).thenReturn(order);

        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminIsLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientIsLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientIsLogged() {
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.findById(existingOrderId);
        });
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.doThrow(ResourceNotFoundException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingOrderId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminIsLogged() {
        Mockito.when(userService.authenticated()).thenReturn(admin);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientIsLogged() {
        Mockito.when(userService.authenticated()).thenReturn(client);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldThrowsUserNotFoundExceptionWhenUserNotLogged() {
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
           OrderDTO result = service.insert(orderDTO);
        });
    }

    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenProductIdDoesNotExist() {
        Mockito.when(userService.authenticated()).thenReturn(client);

        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            OrderDTO result = service.insert(orderDTO);
        });
    }
}
