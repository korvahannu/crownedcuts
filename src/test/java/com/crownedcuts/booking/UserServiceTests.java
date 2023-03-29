package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

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
        Assert.isTrue(user.isPresent(), "UserService did not find user");
        Assert.isTrue(user.get().username().equals("admin@crownedcuts.fi"), "UserService returned wrong user (username)");
        Assert.isTrue(user.get().authorities().contains(new SimpleGrantedAuthority("ADMIN")), "UserService did not return correct roles");
        Assert.isTrue(user.get().authorities().contains(new SimpleGrantedAuthority("USER")), "UserService did not return correct roles");

        user = userService.getUser("nonexistinguser");
        Assert.isTrue(user.isEmpty(), "UserService returned something when it shouldn't have");
    }

    @Test
    void checkPasswordWorks()
    {
        var user = new UserDetails("admin@crownedcuts.fi", "admin", null);
        Assert.isTrue(userService.checkUserPassword(user), "UserService did not correctly return true for correct password");
        user = new UserDetails("admin@crownedcuts.fi", "user", null);
        Assert.isTrue(!userService.checkUserPassword(user), "UserService did not correctly return false for wrong password");
    }
}
