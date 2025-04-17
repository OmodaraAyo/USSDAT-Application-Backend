package main.service.implementations;

import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.exceptions.ValidatorException;
import main.models.users.Company;
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
    private UssdCounterUtil ussdCounterUtil;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);


    @Override
    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        validateRequestData(companyRequest);
        return registerNewCompany(companyRequest);
    }

    private CompanyResponse registerNewCompany(CompanyRequest companyRequest) {
        Company newCompany = new Company();
        newCompany.setCompanyName(companyRequest.getCompanyName());
        newCompany.setCompanyPhone(companyRequest.getCompanyPhone());
        newCompany.setCompanyEmail(companyRequest.getCompanyEmail());
        newCompany.setPassword(bCryptPasswordEncoder.encode(GeneratorUtil.generateKey(16)));
        newCompany.setApiKey(GeneratorUtil.generateKey(32));
        newCompany.setUssdShortCode(generateUssdCode());
        newCompany.setBusinessRegistrationNumber(companyRequest.getBusinessRegistrationNumber());
        newCompany.setCategory(companyRequest.getCategory());
        newCompany.setCreateAt(DateUtil.getCurrentDate());
        newCompany.setUpdateAt(DateUtil.getCurrentDate());
        newCompany.setActive(true);
        newCompany.setFirstLogin(true);
        companyRepo.save(newCompany);
        return new CompanyResponse(){{setSuccess(true);}};
    }

    private void validateRequestData(CompanyRequest companyRequest) {
        ValidatorException.validateCompanyName(companyRequest.getCompanyName());
        ValidatorException.validateEmail(companyRequest.getCompanyEmail());
        ValidatorException.validatePhoneNumber(companyRequest.getCompanyPhone());
    }

    private String generateUssdCode() {
        int ussdCode = ussdCounterUtil.getNextUssdCode();
        return String.valueOf(100 + ussdCode - 1);
    }
}
