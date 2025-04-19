package main.repository;

import main.models.users.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends MongoRepository<Company, String> {
    Company findByCompanyId(String companyId);
    Company findByCompanyName(String name);
    Company findByCompanyEmail(String companyEmail);
}
