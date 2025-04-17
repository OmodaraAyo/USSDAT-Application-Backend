package main.service.implementations;

import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.models.Company;
import main.repository.CompanyAdminRepo;
import main.repository.CompanyRepo;
import main.service.interfaces.CompanyService;
import main.utils.DateUtil;
import main.utils.GeneratorUtil;
import main.utils.UssdCounterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private CompanyAdminRepo companyAdminRepo;

    @Autowired
    private UssdCounterUtil ussdCounterUtil;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);


    @Override
    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        int ussdCode = ussdCounterUtil.getNextUssdCode();
        String newUssdCode = String.valueOf(100 + ussdCode - 1);
        Company newCompany = new Company();
        newCompany.setApiKey(GeneratorUtil.generateApiKey());
        newCompany.setUssdShortCode(newUssdCode);
        newCompany.setCompanyName(companyRequest.getCompanyName());
        newCompany.setCompanyEmail(companyRequest.getCompanyEmail());
        newCompany.setCompanyPhone(companyRequest.getCompanyPhone());
        newCompany.setCategory(companyRequest.getCategory());
        newCompany.setCreateAt(DateUtil.getCurrentDate());
        newCompany.setUpdateAt(DateUtil.getCurrentDate());
        newCompany.setActive(true);

        Company savedCompany = companyRepo.save(newCompany);
//        CompanyResponse companyResponse = new CompanyResponse();
//        companyResponse.setSuccess(Boolean.TRUE);
        return new CompanyResponse(){{setSuccess(true);}};
    }
}
