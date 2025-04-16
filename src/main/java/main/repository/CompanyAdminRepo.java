package main.repository;

import main.models.CompanyAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyAdminRepo extends MongoRepository<CompanyAdmin, String> {

    CompanyAdmin findByCompanyEmail(String companyEmail);

}
