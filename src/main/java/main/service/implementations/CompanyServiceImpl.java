package main.service.implementations;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import main.dtos.DeleteResponse;
import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signOut.LogoutResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.dtos.update.ChangePasswordRequest;
import main.dtos.update.ChangePasswordResponse;
import main.dtos.update.UpdateCompanyRequest;
import main.dtos.update.UpdateCompanyResponse;
import main.exceptions.ValidatorException;
import main.models.enums.Category;
import main.models.enums.Role;
import main.models.users.Company;
import main.repository.CompanyRepo;
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

import java.util.Optional;

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

    @Autowired
    private AuthenticatedCompanyService authenticatedCompanyService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public static String genPass;

    @Override
    public CompanyResponse registerCompany(CompanyRequest companyRequest) {
        validateRequestData(companyRequest);
        doesCompanyExist(companyRequest);
        return registerNewCompany(companyRequest);
    }

    @Override
    public LoginResponse signIn(LoginRequest loginRequest) {
        String requestEmail = loginRequest.getCompanyEmail().toLowerCase();
        checkIfUserIsActive(requestEmail);
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestEmail, loginRequest.getPassword()));
            updateCompanyState(requestEmail);
            return confirmedLoginResponse(loginRequest);
        }catch (BadCredentialsException e) {
            throw new ValidatorException("Bad credentials: Invalid email or password");
        }
    }

    private void checkIfUserIsActive(String requestEmail) {
        Optional<Company> company = Optional.ofNullable(getCompanyByEmail(requestEmail));
        company.ifPresent(this::checkActiveState);
    }

    private void checkActiveState(Company company) {
        if(!company.isActive()){
            throw new RuntimeException("Account is deactivated. Please contact support.");
        }
    }


    @Override
    public CompanyDetailsResponse findCompanyById(String id) {
        Optional<Company> foundCompany = Optional.ofNullable(findById(id));
        if (foundCompany.isPresent()) {
            return companyDetails(foundCompany.get());
        }
        throw new ValidatorException("Company not found");
    }

    @Override
    public CompanyDetailsResponse findCompanyByEmail(String companyEmail) {
        Optional<Company> foundCompany = Optional.ofNullable(getCompanyByEmail(companyEmail));
        if (foundCompany.isPresent()) {
            return companyDetails(foundCompany.get());
        }
        throw new ValidatorException("Company with email "+ companyEmail + " not found");
    }

    @Override
    public UpdateCompanyResponse updateCompanyDetails(UpdateCompanyRequest updateRequest) {
        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();

        validateUpdateRequestData(updateRequest);
        company.setCompanyPhone(updateRequest.getCompanyRequest().getCompanyPhone());
//        company.setCategory(Category.valueOf(updateRequest.getCompanyRequest().getCategory()));
        company.setCompanyApiKey(updateRequest.getCompanyRequest().getCompanyApiKey());
        company.setBaseUrl(updateRequest.getCompanyRequest().getBaseUrl());
//        company.setFirstLogin(updateRequest.isFirstLogin());
        company.setLastLoginDate(updateRequest.getLastLoginDate());
        company.setUpdateAt(DateUtil.getCurrentDate());

        Company savedCompany = companyRepo.save(company);
        return getUpdatedCompany(savedCompany, updateRequest);
    }

    @Override
    public ChangePasswordResponse resetPassword(ChangePasswordRequest request) {
        Company currentLoggedInCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        if(!bCryptPasswordEncoder.matches(request.getOldPassword(), currentLoggedInCompany.getPassword())) {
            throw new ValidatorException("The provided old password does not match our records");
        }
        return updateCompanyPassword(currentLoggedInCompany, request);
    }

    @Override
    public LogoutResponse logOut() {
        Company currentLoggedInCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();

        currentLoggedInCompany.setLoggedIn(false);
        companyRepo.save(currentLoggedInCompany);
        return new LogoutResponse("Logout successful");
    }

    @Override
    public DeleteResponse deleteById() {
        Company deleteCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();

        deleteCompany.setActive(false);
        deleteCompany.setLoggedIn(false);
        deleteCompany.setUpdateAt(DateUtil.getCurrentDate());
        deleteCompany.setLastLoginDate(DateUtil.getCurrentDate());
        companyRepo.save(deleteCompany);
        return new DeleteResponse("Account closed successfully", true);
    }

    private void updateCompanyState(String requestEmail) {
        Company company = getCompanyByEmail(requestEmail);
        company.setLoggedIn(true);
        company.setLastLoginDate(DateUtil.getCurrentDate());
        companyRepo.save(company);
    }

    private ChangePasswordResponse updateCompanyPassword(Company currentLoggedInCompany, ChangePasswordRequest request) {
        currentLoggedInCompany.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        currentLoggedInCompany.setFirstLogin(false);
        companyRepo.save(currentLoggedInCompany);
        return new ChangePasswordResponse("Password changed successfully");
    }


    private UpdateCompanyResponse getUpdatedCompany(Company savedCompany, UpdateCompanyRequest updateRequest) {
        UpdateCompanyResponse response = new UpdateCompanyResponse();
        response.setMessage("Updated Successfully");
        response.getCompanyDetails().setCompanyId(savedCompany.getCompanyId());
        response.getCompanyDetails().setUssdShortCode(savedCompany.getUssdShortCode());
        response.getCompanyDetails().setCompanyName(savedCompany.getCompanyName());
        response.getCompanyDetails().setCompanyPhone(savedCompany.getCompanyPhone());
        response.getCompanyDetails().setCompanyEmail(savedCompany.getCompanyEmail());
        response.getCompanyDetails().setBusinessRegistrationNumber(savedCompany.getBusinessRegistrationNumber());
        response.getCompanyDetails().setCategory(savedCompany.getCategory());
        response.getCompanyDetails().setCompanyApiKey(savedCompany.getCompanyApiKey());
        response.getCompanyDetails().setBaseUrl(savedCompany.getBaseUrl());
        response.getCompanyDetails().setRole(String.valueOf(savedCompany.getRole()));
        response.getCompanyDetails().setActive(savedCompany.isActive());
        response.getCompanyDetails().setFirstLogin(savedCompany.isFirstLogin());
        response.getCompanyDetails().setLoggedIn(savedCompany.isLoggedIn());
        response.getCompanyDetails().setLastLoginDate(savedCompany.getLastLoginDate());
        response.getCompanyDetails().setCreateAt(savedCompany.getCreateAt());
        response.getCompanyDetails().setUpdateAt(savedCompany.getUpdateAt());
        return response;
    }


    private void validateUpdateRequestData(UpdateCompanyRequest updateRequest) {
       ValidatorException.validateUpdateRequestDetails(updateRequest);
    }


    private void doesCompanyExist(CompanyRequest companyRequest) {
        checkByCompanyEmail(companyRequest.getCompanyEmail());
        checkByCompanyName(companyRequest.getCompanyName());
    }

    private void checkByCompanyEmail(String companyEmail) {
        Optional<Company> existing = Optional.ofNullable(getCompanyByEmail(companyEmail));
        if (existing.isPresent()) {
            throw new RuntimeException("An account with this information already exists. Please sign in to access your account.");
        }
    }


    private void checkByCompanyName(String companyName) {
        Optional<Company> existing = Optional.ofNullable(companyRepo.findByCompanyName(companyName.toLowerCase()));
        if (existing.isPresent()) {
            throw new RuntimeException("An account with this information already exists. Please sign in to access your account.");
        }
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
        companyDetailsResponse.setRole(company.getRole().name());
        companyDetailsResponse.setActive(company.isActive());
        companyDetailsResponse.setLoggedIn(company.isLoggedIn());
        companyDetailsResponse.setFirstLogin(company.isFirstLogin());
        companyDetailsResponse.setLastLoginDate(company.getLastLoginDate());
        companyDetailsResponse.setCreateAt(company.getCreateAt());
        companyDetailsResponse.setUpdateAt(company.getUpdateAt());
        return companyDetailsResponse;
    }

    private LoginResponse confirmedLoginResponse(LoginRequest loginRequest) {
        CompanyDetailsResponse getCurrentLoggedInCompany = findCompanyByEmail(loginRequest.getCompanyEmail());
        return createLoginResponse(loginRequest, getCurrentLoggedInCompany);
    }

    private LoginResponse createLoginResponse(LoginRequest loginRequest, CompanyDetailsResponse getCurrentLoggedInCompany) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(loginRequest.getCompanyEmail().toLowerCase()));
        loginResponse.setResponse("Login Successful");
        loginResponse.setIsLoggedIn(true);
        loginResponse.setFirstLogin(getCurrentLoggedInCompany.isFirstLogin());
        return loginResponse;
    }

    private Company findById(String id) {
        return companyRepo.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }

    private Company getCompanyByEmail(String companyEmail) {
        return companyRepo.findByCompanyEmail(companyEmail.toLowerCase());
    }

    private CompanyResponse registerNewCompany(CompanyRequest companyRequest) {
        String generatedPassword = generatePassword();
        Company newCompany = createNewCompany(companyRequest, generatedPassword);
        Company savedCompany = saveCompany(newCompany);
//        mailRegisteredCompany(savedCompany.getCompanyEmail(), generatedPassword);
        return new CompanyResponse("Registration successful! Login credentials will be sent to your email shortly.", savedCompany.getCompanyId(), true, false);
    }

    private Company createNewCompany(CompanyRequest companyRequest, String generatedPassword) {
        Company newCompany = new Company();
        newCompany.setCompanyName(companyRequest.getCompanyName().toLowerCase());
        newCompany.setCompanyPhone(companyRequest.getCompanyPhone());
        newCompany.setCompanyEmail(companyRequest.getCompanyEmail().toLowerCase());
        newCompany.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
        newCompany.setApiKey(GeneratorUtil.generateKey(32));
        newCompany.setUssdShortCode(generateUssdCode());
        newCompany.setBusinessRegistrationNumber(companyRequest.getBusinessRegistrationNumber());
        newCompany.setCategory(Category.getCategory(companyRequest.getCategory()));
        newCompany.setRole(Role.ADMIN);
        newCompany.setCreateAt(DateUtil.getCurrentDate());
        newCompany.setUpdateAt(DateUtil.getCurrentDate());
        newCompany.setActive(true);
        newCompany.setFirstLogin(true);
        newCompany.setLoggedIn(false);
        return newCompany;
    }

    private String generatePassword() {
        genPass = GeneratorUtil.generateKey(16);
        return genPass;
    }

    private Company saveCompany(Company newCompany) {
        try{
            return companyRepo.save(newCompany);
        } catch (DuplicateKeyException | MongoWriteException e) {
            throw new RuntimeException("An account with this information already exists. Please sign in to access your account.");
        }
    }

    private void mailRegisteredCompany(String registeredCompanyEmail, String generatedPassword) {
        emailServiceImpl.sendEmail(registeredCompanyEmail, generatedPassword);
    }

    private void validateRequestData(CompanyRequest companyRequest) {
        ValidatorException.validateCompanyName(companyRequest.getCompanyName());
        ValidatorException.validateEmail(companyRequest.getCompanyEmail());
        ValidatorException.validatePhoneNumber(companyRequest.getCompanyPhone());
        ValidatorException.validateSelectedCategory(companyRequest.getCategory());
        ValidatorException.ensureRequiredFieldsArePresent(companyRequest);
    }

    private String generateUssdCode() {
        int ussdCode = ussdCounterUtil.getNextUssdCode();
        return String.valueOf(100 + ussdCode - 1);
    }
}
