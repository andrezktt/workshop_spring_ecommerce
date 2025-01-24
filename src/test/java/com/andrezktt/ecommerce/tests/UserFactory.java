package com.andrezktt.ecommerce.tests;

import com.andrezktt.ecommerce.entities.Role;
import com.andrezktt.ecommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static User createClientUser() {
        User user = new User(1L, "Maria", "maria@gmail.com", "11911111111", LocalDate.parse("2001-03-15"), "$2a$10$y677XyoknmSsCr98dd3fJeMmCMavWOag8QHpNJ18oZqU4HkeTgXiK");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createAdminUser() {
        User user = new User(2L, "Alex", "alex@gmail.com", "11922222222", LocalDate.parse("1999-07-23"), "$2a$10$y677XyoknmSsCr98dd3fJeMmCMavWOag8QHpNJ18oZqU4HkeTgXiK");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }

    public static User createCustomClientUser(Long id, String username) {
        User user = new User(id, "Maria", username, "11911111111", LocalDate.parse("2001-03-15"), "$2a$10$y677XyoknmSsCr98dd3fJeMmCMavWOag8QHpNJ18oZqU4HkeTgXiK");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createCustomAdminUser(Long id, String username) {
        User user = new User(id, "Alex", username, "11922222222", LocalDate.parse("1999-07-23"), "$2a$10$y677XyoknmSsCr98dd3fJeMmCMavWOag8QHpNJ18oZqU4HkeTgXiK");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
}
