package main.repositories;

import main.models.companies.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepo extends MongoRepository<Company, String> {
    Company findByCompanyId(String companyId);
    Company findByCompanyName(String name);
    Company findByCompanyEmail(String companyEmail);


    Optional<Company> findByUssdShortCode(String subCode);
}
