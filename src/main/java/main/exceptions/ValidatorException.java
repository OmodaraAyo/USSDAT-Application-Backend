package main.exceptions;


import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.CreateOptionRequest;
import main.dtos.requests.companyFaceRequest.UpdateCompanyRequest;
import main.models.enums.Category;
import main.models.companies.Company;
import main.models.companies.Option;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;

public class ValidatorException extends RuntimeException {

    //    private static final String VALID_EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String NIGERIA_PHONE_NUMBER_PATTERN = "^(?!.*[^0-9+].*$)\\+?(234|0)[7-9][01][0-9]{8}$";

    public ValidatorException(String message) {
        super(message);
    }

    public static void validateCompanyName(String companyName){
        String pattern = ".*[@#$%,!?/\\\\].*";

        if(companyName == null || companyName.isEmpty() || companyName.trim().isEmpty()){
            throw new ValidatorException("Company name is required");
        }
        if(companyName.matches(pattern)){
            throw new ValidatorException("The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.");
        }
    }
    public static void validateEmail(String email){
        EmailValidator emailValidator = EmailValidator.getInstance(false, true);

        if(email == null || email.isEmpty() || email.trim().isEmpty()){
            throw new ValidatorException("Email is required");
        }
        if(!emailValidator.isValid(email.trim())){
            throw new ValidatorException("Invalid email address.");
        }
    }

    public static void validatePhoneNumber(List<String> phoneNumbers) {

        for(String phoneNumber : phoneNumbers){

            if(phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.trim().isEmpty()){
                throw new ValidatorException("Phone number is required");
            }
            if(!phoneNumber.matches(NIGERIA_PHONE_NUMBER_PATTERN)){
                throw new ValidatorException("Invalid phone number. Please try again");
            }

        }
    }

    public static void ensureRequiredFieldsArePresent(CompanySignUpRequest companySignUpRequest){

        String regNumber = companySignUpRequest.getBusinessRegistrationNumber();

        if(regNumber == null || regNumber.isEmpty() || regNumber.trim().isEmpty()){
            throw new ValidatorException("Business registration number is required");
        }
        if(companySignUpRequest.getCategory() == null){
            throw new ValidatorException("You must select a category");
        }
    }

    public static void validateSelectedCategory(String selectedCategory){
        Category.getCategory(selectedCategory);
    }

    public static void validateUpdateRequestDetails(UpdateCompanyRequest request){
        if(request.getCompanyRequest().getCompanyPhone() == null || request.getCompanyRequest().getCompanyPhone().isEmpty()) {
            throw new ValidatorException("At least one phone number is required");
        }
        if(request.getCompanyRequest().getCategory() == null || request.getCompanyRequest().getCategory().trim().isEmpty()) {
            throw new ValidatorException("Category is required");
        }
        if(request.getCompanyRequest().getCompanyApiKey() == null || request.getCompanyRequest().getCompanyApiKey().trim().isEmpty()) {
            throw new ValidatorException("Api key is required");
        }
        if(request.getCompanyRequest().getBaseUrl() == null || request.getCompanyRequest().getBaseUrl().trim().isEmpty()) {
            throw new ValidatorException("Base url is required");
        }
    }

    public static void validateOptionRequest(String optionRequest) {
        if(optionRequest == null || optionRequest.trim().isEmpty()){
            throw new ValidatorException("Please enter a menu title.");
        }
    }

    public static void validateDuplicateTitle(Company activeCompanySession, CreateOptionRequest optionRequest) {
        for (Option option : activeCompanySession.getMenu().getOptions()) {
            if (option.getTitle().equalsIgnoreCase(optionRequest.getTitle())) {
                throw new ValidatorException("Oops! A menu titled 'Register' already exists. Please try another name.");
            }
        }
    }

    public static void validateId(String companyId, String companyId1) {
        if(companyId == null || companyId1.isEmpty() || companyId.trim().isEmpty()){
            throw new ValidatorException("Company id is required");
        }
        if(!companyId.equals(companyId1)){
            throw new ValidatorException("Id mismatch");
        }
    }
}
