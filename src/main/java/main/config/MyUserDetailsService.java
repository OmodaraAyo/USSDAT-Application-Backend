package main.config;

import main.models.companies.Company;
import main.models.security.CompanyPrincipal;
import main.repositories.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final CompanyRepo companyRepo;

    @Autowired
    public MyUserDetailsService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String companyEmail) throws UsernameNotFoundException {
        Company company = companyRepo.findByCompanyEmail(companyEmail);
        if (company == null) {
            throw new UsernameNotFoundException("Company with account: " +companyEmail+" not found");
        }
        return new CompanyPrincipal(company);
    }
}
