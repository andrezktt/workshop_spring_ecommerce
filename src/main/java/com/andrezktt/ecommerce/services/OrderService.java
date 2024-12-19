package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.OrderDTO;
import com.andrezktt.ecommerce.dto.OrderItemDTO;
import com.andrezktt.ecommerce.entities.Order;
import com.andrezktt.ecommerce.entities.OrderItem;
import com.andrezktt.ecommerce.entities.OrderStatus;
import com.andrezktt.ecommerce.entities.Product;
import com.andrezktt.ecommerce.repositories.OrderItemRepository;
import com.andrezktt.ecommerce.repositories.OrderRepository;
import com.andrezktt.ecommerce.repositories.ProductRepository;
import com.andrezktt.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado."));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(userService.authenticated());
        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDTO.getProductId());
            order.getItems().add(new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice()));
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
