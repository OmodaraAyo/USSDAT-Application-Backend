package main.helper;

import main.models.companies.Company;
import main.repositories.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyUpdateSaverImpl implements CompanyUpdateSaver {

    @Autowired
    private CompanyRepo companyRepo;

    @Override
    public Company saveUpdatedCompany(Company company) {
        return companyRepo.save(company);
    }
}
