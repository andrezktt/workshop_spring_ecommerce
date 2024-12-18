package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.dto.OrderDTO;
import com.andrezktt.ecommerce.repositories.OrderRepository;
import com.andrezktt.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        return new OrderDTO(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado.")));
    }
}
