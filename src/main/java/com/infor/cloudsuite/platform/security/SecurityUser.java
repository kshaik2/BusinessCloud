package com.infor.cloudsuite.platform.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 10/13/11 1:31 PM
 */
public class SecurityUser implements UserDetails {
    private Long id;
    private Collection<GrantedAuthority> authorities;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String company;
    private Locale language;
    private Long createTime;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public SecurityUser(User user) {
        this.id = user.getId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        if (user.getCompany()!=null) {
        	this.company=user.getCompany().getName();
        }
        this.language = (null == user.getLanguage() ? Locale.US : StringUtils.parseLocaleString(user.getLanguage()));
        final long createAtTime = user.getCreatedAt().getTime();
        this.createTime = (createAtTime - (createAtTime % StringDefs.TIME_CORRECT));
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = user.getActive();
        this.authorities = getAuthorities(user.getRoles());
    }

    private Collection<GrantedAuthority> getAuthorities(Set<Role> roles) {
        Collection<GrantedAuthority> authorities = new HashSet<>(roles.size());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Locale getLanguage() {
        if (null == language) {
            return Locale.US;
        }
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isExternalUser() {
        //noinspection SuspiciousMethodCalls
        return containsRole(StringDefs.ROLE_EXTERNAL);
    }

    public boolean isAdmin() {
        //noinspection SuspiciousMethodCalls
        return containsRole(StringDefs.ROLE_ADMIN);
    }

    public boolean isInfor24Admin() {
        return containsRole(StringDefs.ROLE_I24_ADMIN);
    }

    public boolean isSales() {
        return containsRole(StringDefs.ROLE_SALES);
    }
    
    public boolean isSuperAdmin() {
    	return containsRole(StringDefs.ROLE_SUPERADMIN);
    }

    private boolean containsRole(String role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
