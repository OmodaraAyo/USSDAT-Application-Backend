package main.config;

import main.models.Company;
import main.models.CompanyAdmin;
import main.models.CompanyPrincipal;
import main.repository.CompanyAdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final CompanyAdminRepo companyAdminRepo;

    @Autowired
    public MyUserDetailsService(CompanyAdminRepo companyAdminRepo) {
        this.companyAdminRepo = companyAdminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String companyEmail) throws UsernameNotFoundException {
        CompanyAdmin company = companyAdminRepo.findByCompanyEmail(companyEmail);
        if (company == null) {
            throw new UsernameNotFoundException("Company with account:" +companyEmail+" not found");
        }
        return new CompanyPrincipal(company);
    }
}
