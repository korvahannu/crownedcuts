package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserDetailsTests
{
    @Test
    void worksWithManyRoles()
    {
        UserDetails.of("admin@crownedcuts.fi", "admin", "ADMIN", "USER");
    }

    @Test
    void worksWithNoRoles()
    {
        UserDetails.of("admin@crownedcuts.fi", "admin");
    }

    @Test
    void nullInformationShouldNotThrow()
    {
        UserDetails.of(null, null);
    }

    @Test
    void shouldThrowWithIncorrectUsernameFormatting()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("@crownedcuts.fi", "admin");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("", "admin");
        });
    }

    @Test
    void InvalidRoleFormattingShouldThrow()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", " ");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", "admin");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            UserDetails.of("admin@crownedcuts.fi", "admin", "USER", "AD MIN");
        });
    }
}
