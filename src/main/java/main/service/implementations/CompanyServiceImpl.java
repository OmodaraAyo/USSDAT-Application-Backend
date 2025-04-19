package main.service.implementations;

import main.dtos.company.CompanyDetailsRequest;
import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.exceptions.ValidatorException;
import main.models.enums.Category;
import main.models.users.Company;
import main.repository.CompanyRepo;
import main.service.JWTService;
import main.service.interfaces.CompanyService;
import main.utils.DateUtil;
import main.utils.GeneratorUtil;
import main.utils.UssdCounterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private UssdCounterUtil ussdCounterUtil;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public static String genPass;

    @Override
    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        validateRequestData(companyRequest);
        return registerNewCompany(companyRequest);
    }

    @Override
    public LoginResponse signIn(LoginRequest loginRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getCompanyEmail(), loginRequest.getPassword()));
            return confirmedLoginResponse(loginRequest);
        }catch (BadCredentialsException e) {
            throw new ValidatorException("Bad credentials: Invalid email or password");
        }
    }

    @Override
    public CompanyDetailsResponse findCompanyByEmail(CompanyDetailsRequest companyDetailsRequest) {
        for (Company company : companyRepo.findAll()) {
            if(company.getCompanyEmail().equalsIgnoreCase(companyDetailsRequest.getEmail())) {
                return companyDetails(company);
            }
        }
        throw new ValidatorException("Company with email "+ companyDetailsRequest.getEmail() + " not found");
    }

    private CompanyDetailsResponse companyDetails(Company company) {
        CompanyDetailsResponse companyDetailsResponse = new CompanyDetailsResponse();
        companyDetailsResponse.setCompanyId(company.getCompanyId());
        companyDetailsResponse.setUssdShortCode(company.getUssdShortCode());
        companyDetailsResponse.setCompanyName(company.getCompanyName());
        companyDetailsResponse.setCompanyPhone(company.getCompanyPhone());
        companyDetailsResponse.setCompanyEmail(company.getCompanyEmail());
        companyDetailsResponse.setBusinessRegistrationNumber(company.getBusinessRegistrationNumber());
        companyDetailsResponse.setCategory(company.getCategory());
        companyDetailsResponse.setApiKey(company.getApiKey());
        companyDetailsResponse.setBaseUrl(company.getBaseUrl());
        companyDetailsResponse.setActive(company.isActive());
        companyDetailsResponse.setFirstLogin(company.isFirstLogin());
        companyDetailsResponse.setLastLoginDate(company.getLastLoginDate());
        companyDetailsResponse.setCreateAt(company.getCreateAt());
        companyDetailsResponse.setUpdateAt(company.getUpdateAt());
        return companyDetailsResponse;
    }

    private LoginResponse confirmedLoginResponse(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(loginRequest.getCompanyEmail()));
        loginResponse.setResponse("Login Successful");
        loginResponse.setIsLoggedIn(true);
        return loginResponse;
    }

    private CompanyResponse registerNewCompany(CompanyRequest companyRequest) {
        String generatedPassword = generatePassword();
        Company newCompany = createNewCompany(companyRequest, generatedPassword);
        Company savedCompany = saveCompany(newCompany);
//        mailRegisteredCompany(savedCompany.getCompanyEmail(), generatedPassword);
        return new CompanyResponse("Registration successful! Login credentials will be sent to your email shortly.", true);
    }

    private Company createNewCompany(CompanyRequest companyRequest, String generatedPassword) {
        Company newCompany = new Company();
        newCompany.setCompanyName(companyRequest.getCompanyName());
        newCompany.setCompanyPhone(companyRequest.getCompanyPhone());
        newCompany.setCompanyEmail(companyRequest.getCompanyEmail());
        newCompany.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
        newCompany.setApiKey(GeneratorUtil.generateKey(32));
        newCompany.setUssdShortCode(generateUssdCode());
        newCompany.setBusinessRegistrationNumber(companyRequest.getBusinessRegistrationNumber());
        newCompany.setCategory(companyRequest.getCategory());
        newCompany.setCreateAt(DateUtil.getCurrentDate());
        newCompany.setUpdateAt(DateUtil.getCurrentDate());
        newCompany.setActive(true);
        newCompany.setFirstLogin(true);
        return newCompany;
    }

    private String generatePassword() {
        genPass = GeneratorUtil.generateKey(16);
        return genPass;
    }

    private Company saveCompany(Company newCompany) {
        return companyRepo.save(newCompany);
    }

    private void mailRegisteredCompany(String registeredCompanyEmail, String generatedPassword) {
        emailServiceImpl.sendEmail(registeredCompanyEmail, generatedPassword);
    }

    private void validateRequestData(CompanyRequest companyRequest) {
        ValidatorException.validateCompanyName(companyRequest.getCompanyName());
        ValidatorException.validateEmail(companyRequest.getCompanyEmail());
        ValidatorException.validatePhoneNumber(companyRequest.getCompanyPhone());
        ValidatorException.ensureRequiredFieldsArePresent(companyRequest);
    }

    private String generateUssdCode() {
        int ussdCode = ussdCounterUtil.getNextUssdCode();
        return String.valueOf(100 + ussdCode - 1);
    }
}
