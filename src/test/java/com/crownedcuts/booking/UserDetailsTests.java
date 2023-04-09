package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserDetailsTests
{
    @Test
    void worksWithManyRoles()
    {
        var user = UserDetails.of("admin@crownedcuts.fi", "admin", "ADMIN", "USER");
        Assertions.assertEquals("admin@crownedcuts.fi", user.username());
    }

    @Test
    void worksWithNoRoles()
    {
        var user = UserDetails.of("admin@crownedcuts.fi", "admin", "admin", "admin");
        Assertions.assertEquals("admin@crownedcuts.fi", user.username());
    }

    @Test
    void nullInformationShouldNotThrow()
    {
        var user = UserDetails.of(null, null, null, null);
        Assertions.assertNull(user.username());
    }

    @Test
    void shouldThrowWithIncorrectUsernameFormatting()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin", "admin", "admin", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@", "admin", "admin", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("@crownedcuts.fi", "admin", "admin", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("", "admin", "admin", "admin");
        });
    }

    @Test
    void InvalidRoleFormattingShouldThrow()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", " ", "admin", "admin");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", "admin", "admin", "admin");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", "admin", "admin", "USER", "AD MIN");
        });
    }
}
