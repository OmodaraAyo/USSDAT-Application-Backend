package main.service.implementations.companySide;

import main.models.companies.Company;
import main.repositories.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedCompanyService {

        @Autowired
        private CompanyRepo companyRepo;

        public Company getCurrentAuthenticatedCompany() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null || !authentication.isAuthenticated()) {
                throw new AuthenticationServiceException("Authentication required.");
            }

            Object principal = authentication.getPrincipal();
            String email;

            if(principal instanceof UserDetails) {
                email = ((UserDetails)principal).getUsername();
            }else{
                email = principal.toString();
            }
            Company company = companyRepo.findByCompanyEmail(email);
            if(company == null) {
                throw new AuthenticationServiceException("Authenticated company not found");
            }
            return company;
        }

}
