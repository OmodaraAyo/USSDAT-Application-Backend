package main.service.implementations.companySide;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.exceptions.ValidatorException;
import main.models.companies.Company;
import main.models.enums.Category;
import main.models.enums.Role;
import main.repositories.CompanyRepo;
import main.service.interfaces.companySide.CompanyService;
import main.service.interfaces.companySide.EmailService;
import main.service.interfaces.companySide.MenuService;
import main.utils.DateUtil;
import main.utils.GeneratorUtil;
import main.utils.UssdCounterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private UssdCounterUtil ussdCounterUtil;

    @Autowired
    private EmailService emailServiceImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MenuService menuService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticatedCompanyService authenticatedCompanyService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public static String genPass;

    @Override
    public CompanySignUpResponse registerCompany(CompanySignUpRequest companySignUpRequest) {
        validateRequestData(companySignUpRequest);
        doesCompanyExist(companySignUpRequest);
        return registerNewCompany(companySignUpRequest);
    }

    @Override
    public LoginResponse signIn(LoginRequest loginRequest) {
        String requestEmail = loginRequest.getCompanyEmail().toLowerCase();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestEmail, loginRequest.getPassword()));
            updateCompanyState(requestEmail);
            return confirmedLoginResponse(loginRequest);
        } catch(DisabledException e){
            throw new ValidatorException("Account is deactivated. Please contact support.");
        } catch (BadCredentialsException e) {
            throw new ValidatorException("Bad credentials: Invalid email or password");
        }
    }

    @Override
    public CompanyDetailsResponse findCompanyById(String id) {
        Company authenticatedCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(id, authenticatedCompany.getCompanyId());
        Optional<Company> foundCompany = Optional.ofNullable(findById(authenticatedCompany.getCompanyId()));
        if (foundCompany.isPresent()) {
            return companyDetails(foundCompany.get());
        }
        throw new ValidatorException("Company not found");
    }

    @Override
    public CompanyDetailsResponse findCompanyByEmail(String companyEmail) {
        Optional<Company> foundCompany = Optional.ofNullable(fetchCompanyByEmail(companyEmail));
        if (foundCompany.isPresent()) {
            return companyDetails(foundCompany.get());
        }
        throw new ValidatorException("Company with email "+ companyEmail + " not found");
    }

    @Override
    public UpdateCompanyResponse updateCompanyDetails(String companyId, UpdateCompanyRequest updateRequest) {
        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, company.getCompanyId());
        validateUpdateRequestData(updateRequest);
        company.setCompanyPhone(updateRequest.getCompanyPhone());
        company.setCategory(Category.getCategory(updateRequest.getCategory()));
        company.setCompanyApiKey(updateRequest.getCompanyApiKey());
        company.setBaseUrl(updateRequest.getBaseUrl());
        company.setLastLoginDate(company.getLastLoginDate());
        company.setUpdateAt(DateUtil.getCurrentDate());
        Company savedCompany = companyRepo.save(company);
        return getUpdatedCompany(savedCompany, updateRequest);
    }

    @Override
    public ChangePasswordResponse resetPassword(String companyId, ChangePasswordRequest changePasswordRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        if(!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), activeCompanySession.getPassword())) {
            throw new ValidatorException("The provided old password does not match our records");
        }
        return updateCompanyPassword(activeCompanySession, changePasswordRequest);
    }

    @Override
    public LogoutResponse logOut(String companyId) {
        Company currentLoggedInCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, currentLoggedInCompany.getCompanyId());
        currentLoggedInCompany.setLoggedIn(false);
        companyRepo.save(currentLoggedInCompany);
        return new LogoutResponse("Logout successful");
    }

    @Override
    public DeleteCompanyAccountResponse deactivateCompany(String companyId) {
        Company deleteCompany = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, deleteCompany.getCompanyId());
        deleteCompany.setActive(false);
        deleteCompany.setLoggedIn(false);
        deleteCompany.setUpdateAt(DateUtil.getCurrentDate());
        deleteCompany.setLastLoginDate(DateUtil.getCurrentDate());
        companyRepo.save(deleteCompany);
        return new DeleteCompanyAccountResponse("Account closed successfully", true);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepo.findAll();
    }

    @Override
    public void deleteAllCompanies() {
        companyRepo.deleteAll();
    }

    @Override
    public Company getByCompanyId(String id) {
        return findById(id);
    }


    @Override
    public Company saveCompany(Company company) {
        return companyRepo.save(company);
    }

    @Override
    public Company getByCompanyEmail(String email) {
        return companyRepo.findByCompanyEmail(email);
    }

    private void updateCompanyState(String requestEmail) {
        Company company = fetchCompanyByEmail(requestEmail);
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
        response.setCompanyId(savedCompany.getCompanyId());
//        response.getCompanyDetails().setCompanyId(savedCompany.getCompanyId());
//        response.getCompanyDetails().setUssdShortCode(savedCompany.getUssdShortCode());
//        response.getCompanyDetails().setCompanyName(savedCompany.getCompanyName());
//        response.getCompanyDetails().setCompanyPhone(savedCompany.getCompanyPhone());
//        response.getCompanyDetails().setCompanyEmail(savedCompany.getCompanyEmail());
//        response.getCompanyDetails().setBusinessRegistrationNumber(savedCompany.getBusinessRegistrationNumber());
//        response.getCompanyDetails().setCategory(savedCompany.getCategory());
//        response.getCompanyDetails().setCompanyApiKey(savedCompany.getCompanyApiKey());
//        response.getCompanyDetails().setBaseUrl(savedCompany.getBaseUrl());
//        response.getCompanyDetails().setRole(String.valueOf(savedCompany.getRole()));
//        response.getCompanyDetails().setActive(savedCompany.isActive());
//        response.getCompanyDetails().setFirstLogin(savedCompany.isFirstLogin());
//        response.getCompanyDetails().setLoggedIn(savedCompany.isLoggedIn());
//        response.getCompanyDetails().setLastLoginDate(savedCompany.getLastLoginDate());
//        response.getCompanyDetails().setCreateAt(savedCompany.getCreateAt());
//        response.getCompanyDetails().setUpdateAt(savedCompany.getUpdateAt());
        return response;
    }


    private void validateUpdateRequestData(UpdateCompanyRequest updateRequest) {
        ValidatorException.validateUpdateRequestDetails(updateRequest);
    }


    private void doesCompanyExist(CompanySignUpRequest companySignUpRequest) {
        checkByCompanyEmail(companySignUpRequest.getCompanyEmail());
        checkByCompanyName(companySignUpRequest.getCompanyName());
    }

    private void checkByCompanyEmail(String companyEmail) {
        Optional<Company> existing = Optional.ofNullable(fetchCompanyByEmail(companyEmail));
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
        if(company.isFirstLogin()){
            return companyDetailsWithWarning(company);
        }
        return companyDetailsWithoutWarning(company);
    }

    private CompanyDetailsResponse companyDetailsWithoutWarning(Company company) {
        CompanyDetailsResponse companyDetailsResponse = new CompanyDetailsResponse();
        setCompanyDetailsResponse(company, companyDetailsResponse);
        return companyDetailsResponse;
    }

    private void setCompanyDetailsResponse(Company company, CompanyDetailsResponse companyDetailsResponse) {
        companyDetailsResponse.setCompanyId(company.getCompanyId());
        companyDetailsResponse.setUssdShortCode(company.getUssdShortCode());
        companyDetailsResponse.setCompanyName(company.getCompanyName());
        companyDetailsResponse.setCompanyPhone(company.getCompanyPhone());
        companyDetailsResponse.setCompanyEmail(company.getCompanyEmail());
        companyDetailsResponse.setBusinessRegistrationNumber(company.getBusinessRegistrationNumber());
        companyDetailsResponse.setCategory(company.getCategory());
        companyDetailsResponse.setApiKey(company.getApiKey());
        companyDetailsResponse.setCompanyApiKey(company.getCompanyApiKey());
        companyDetailsResponse.setBaseUrl(company.getBaseUrl());
        companyDetailsResponse.setRole(company.getRole().name());
        companyDetailsResponse.setActive(company.isActive());
        companyDetailsResponse.setLoggedIn(company.isLoggedIn());
        companyDetailsResponse.setFirstLogin(company.isFirstLogin());
        companyDetailsResponse.setLastLoginDate(company.getLastLoginDate());
        companyDetailsResponse.setCreateAt(company.getCreateAt());
        companyDetailsResponse.setUpdateAt(company.getUpdateAt());
    }

    private CompanyDetailsResponse companyDetailsWithWarning(Company company) {
        CompanyDetailsResponse companyDetailsResponse = new CompanyDetailsResponse();
        setCompanyDetailsResponse(company, companyDetailsResponse);
        companyDetailsResponse.setWarning("Your default password is temporary and expires after first use. Please set a new password.");
        return companyDetailsResponse;
    }

    private LoginResponse confirmedLoginResponse(LoginRequest loginRequest) {
        CompanyDetailsResponse getCurrentLoggedInCompany = findCompanyByEmail(loginRequest.getCompanyEmail());
        return createLoginResponse(loginRequest, getCurrentLoggedInCompany);
    }

    private LoginResponse createLoginResponse(LoginRequest loginRequest, CompanyDetailsResponse getCurrentLoggedInCompany) {
        if(getCurrentLoggedInCompany.isFirstLogin()){
            return createFirstLoginResponseWithWarning(loginRequest, getCurrentLoggedInCompany);
        }
        return createLoginResponseWithoutWarning(loginRequest, getCurrentLoggedInCompany);
    }

    private LoginResponse createLoginResponseWithoutWarning(LoginRequest loginRequest, CompanyDetailsResponse getCurrentLoggedInCompany) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setX_y_z(jwtService.generateToken(loginRequest.getCompanyEmail().toLowerCase(), getCurrentLoggedInCompany.getRole()));
        loginResponse.setResponse("Login Successful");
        loginResponse.setIsLoggedIn(true);
        loginResponse.setFirstLogin(getCurrentLoggedInCompany.isFirstLogin());
        return loginResponse;
    }

    private LoginResponse createFirstLoginResponseWithWarning(LoginRequest loginRequest, CompanyDetailsResponse getCurrentLoggedInCompany) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setX_y_z(jwtService.generateToken(loginRequest.getCompanyEmail().toLowerCase(), getCurrentLoggedInCompany.getRole()));
        loginResponse.setRef(getCurrentLoggedInCompany.getCompanyId());
        loginResponse.setResponse("Login Successful");
        loginResponse.setWarning("Your default password is temporary and expires after first use. Please set a new password.");
        loginResponse.setIsLoggedIn(true);
        loginResponse.setFirstLogin(getCurrentLoggedInCompany.isFirstLogin());
        return loginResponse;
    }

    private Company findById(String id) {
        return companyRepo.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }

    private Company fetchCompanyByEmail(String companyEmail) {
        return companyRepo.findByCompanyEmail(companyEmail.toLowerCase());
    }

    private CompanySignUpResponse registerNewCompany(CompanySignUpRequest companySignUpRequest) {
        String generatedPassword = generatePassword();
        Company newCompany = createNewCompany(companySignUpRequest, generatedPassword);
        Company savedCompany = saveNewCompany(newCompany);

        mailRegisteredCompany(savedCompany.getCompanyName(), savedCompany.getCompanyEmail(), generatedPassword);
        return new CompanySignUpResponse("Registration successful! Login credentials will be sent to your email shortly.", savedCompany.getCompanyId(), true, false);
    }

    private Company createNewCompany(CompanySignUpRequest companySignUpRequest, String generatedPassword) {
        Company newCompany = new Company();
        newCompany.setCompanyName(companySignUpRequest.getCompanyName().toLowerCase());
        newCompany.setCompanyPhone(companySignUpRequest.getCompanyPhone());
        newCompany.setCompanyEmail(companySignUpRequest.getCompanyEmail().toLowerCase());
        newCompany.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
        newCompany.setApiKey(GeneratorUtil.generateKey(32));
        newCompany.setUssdShortCode(generateUssdCode());
        newCompany.setBusinessRegistrationNumber(companySignUpRequest.getBusinessRegistrationNumber());
        newCompany.setCategory(Category.getCategory(companySignUpRequest.getCategory()));
        newCompany.setRole(Role.ADMIN);
        newCompany.setMenu(menuService.createDefaultMenu());
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

    private Company saveNewCompany(Company newCompany) {
        try{
            return companyRepo.save(newCompany);
        } catch (DuplicateKeyException | MongoWriteException e) {
            throw new RuntimeException("An account with this information already exists. Please sign in to access your account.");
        }
    }

    private void mailRegisteredCompany(String companyName, String registeredCompanyEmail, String generatedPassword) {
        emailServiceImpl.sendEmail(companyName, registeredCompanyEmail, generatedPassword);
    }

    private void validateRequestData(CompanySignUpRequest companySignUpRequest) {
        ValidatorException.validateCompanyName(companySignUpRequest.getCompanyName());
        ValidatorException.validateEmail(companySignUpRequest.getCompanyEmail());
        ValidatorException.validatePhoneNumber(companySignUpRequest.getCompanyPhone());
        ValidatorException.validateSelectedCategory(companySignUpRequest.getCategory());
        ValidatorException.ensureRequiredFieldsArePresent(companySignUpRequest);
    }

    private String generateUssdCode() {
        int ussdCode = ussdCounterUtil.getNextUssdCode();
        return String.valueOf(100 + ussdCode - 1);
    }
}
