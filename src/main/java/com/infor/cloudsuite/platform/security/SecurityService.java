package com.infor.cloudsuite.platform.security;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/25/11 3:32 PM
 */
@Service
public class SecurityService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private CSUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Fully authenticate a user programmatically
     *
     * @param username email username
     * @param password plain text password
     */
    public void fullAccessLogin(String username, String password) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        Authentication token = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Grant a security role to the current user.
     *
     * @param role the Role to grant.
     */
    public void authenticateTemporary(String role) {
        logger.debug("Validating an anonymous user with role {}.", role);
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        RunAsUserToken runAsUser = new RunAsUserToken("validationKey", currentAuth.getPrincipal(), currentAuth.getCredentials(), authorities, currentAuth.getClass());
        authenticationManager.authenticate(runAsUser);
        SecurityContextHolder.getContext().setAuthentication(runAsUser);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void forceLogout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public void forceLogout(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        forceLogout();
    }

    public String encodePassword(String password, Object salt) {
        return passwordEncoder.encodePassword(password.trim(), salt);
    }

    public String encodePassword(String password, Date salt) {
        //Database rounding issues force a truncation of the time for salting.
        Long newSalt = (salt.getTime() - (salt.getTime() % StringDefs.TIME_CORRECT));

        return encodePassword(password.trim(), newSalt);
    }

    public SecurityUser getCurrentUser() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (SecurityUser.class.isAssignableFrom(principal.getClass())) {
            return (SecurityUser) principal;
        }
        return null;
    }
}
