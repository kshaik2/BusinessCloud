package com.infor.cloudsuite.platform.security;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User: bcrow
 * Date: 10/14/11 7:55 PM
 */
public class ValidationAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails details = (UserDetails) authentication.getPrincipal();
        if (details.getPassword() == null || details.getPassword().isEmpty()) {
            authentication.setAuthenticated(true);
        }
        final Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
        authorities.clear();
        authorities.add(new SimpleGrantedAuthority("NOT_VALIDATED"));
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
