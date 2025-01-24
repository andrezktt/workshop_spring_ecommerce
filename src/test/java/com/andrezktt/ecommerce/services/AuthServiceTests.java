package com.andrezktt.ecommerce.services;

import com.andrezktt.ecommerce.entities.User;
import com.andrezktt.ecommerce.services.exceptions.ForbiddenException;
import com.andrezktt.ecommerce.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserService userService;

    private User admin;
    private User selfClient;
    private User otherClient;

    @BeforeEach
    void setUp() throws Exception {
        admin = UserFactory.createAdminUser();
        selfClient = UserFactory.createCustomClientUser(1L, "bob@gmail.com");
        otherClient = UserFactory.createCustomClientUser(3L, "ana@gmail.com");
    }

    @Test
    public void ValidateSelfOrAdminShouldDoNothingWhenAdminIsLogged() {
        Mockito.when(userService.authenticated()).thenReturn(admin);

        Long userId = admin.getId();

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(userId);
        });
    }

    @Test
    public void ValidateSelfOrAdminShouldDoNothingWhenSelfLogged() {
        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = selfClient.getId();

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(userId);
        });
    }

    @Test
    public void ValidateSelfOrAdminThrowsForbiddenExceptionWhenOtherClientIsLogged() {
        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = otherClient.getId();

        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.validateSelfOrAdmin(userId);
        });
    }
}
