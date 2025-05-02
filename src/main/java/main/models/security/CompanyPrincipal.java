package main.models.security;

import main.models.companies.Company;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CompanyPrincipal implements UserDetails {

    private final Company company;

    public CompanyPrincipal(Company company) {
        this.company = company;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (company.getRole() == null) {
            return Collections.emptyList();
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + company.getRole().name()));
    }

    @Override
    public String getPassword() {
        return company.getPassword();
    }

    @Override
    public String getUsername() {
        return company.getCompanyEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return company.isActive();
    }
}
