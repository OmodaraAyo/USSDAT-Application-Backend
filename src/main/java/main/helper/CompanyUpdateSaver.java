package main.helper;

import main.models.companies.Company;

public interface CompanyUpdateSaver {
    Company saveUpdatedCompany(Company company);
}
