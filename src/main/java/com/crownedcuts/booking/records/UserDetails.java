package com.crownedcuts.booking.records;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserDetails(String username, Collection<? extends GrantedAuthority> authorities)
{
}
