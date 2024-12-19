package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.entities.User;
import com.andrezktt.ecommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserService userService;

    public void validateSelfOrAdmin(Long userId) {
        User user = userService.authenticated();
        if (!user.hasRole("ROLE_ADMIN") && !user.getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado!!");
        }
    }
}
