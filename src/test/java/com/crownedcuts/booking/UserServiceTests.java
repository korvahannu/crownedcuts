package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTests
{
    private final UserService userService;

    @Autowired
    public UserServiceTests(UserService userService)
    {
        this.userService = userService;
    }

    @Test
    void gettingUserWorks()
    {
        var user = userService.getUser("admin@crownedcuts.fi");
        Assertions.assertTrue(user.isPresent(), "UserService did not find user");
        Assertions.assertEquals("admin@crownedcuts.fi", user.get().username(), "UserService returned wrong user (username)");
        Assertions.assertTrue(user.get().authorities().contains(new SimpleGrantedAuthority("ADMIN")), "UserService did not return correct roles");
        Assertions.assertTrue(user.get().authorities().contains(new SimpleGrantedAuthority("USER")), "UserService did not return correct roles");

        user = userService.getUser("nonexistinguser");
        Assertions.assertTrue(user.isEmpty(), "UserService returned something when it shouldn't have");
    }

    @Test
    void checkPasswordWorks()
    {
        var user = UserDetails.of("admin@crownedcuts.fi", "admin");
        Assertions.assertTrue(userService.checkUserPassword(user), "UserService did not correctly return true for correct password");
        user = UserDetails.of("admin@crownedcuts.fi", "user");
        Assertions.assertFalse(userService.checkUserPassword(user), "UserService did not correctly return false for wrong password");
    }
}
