package main.exceptions;


import org.apache.commons.validator.routines.EmailValidator;

public class ValidatorException extends RuntimeException {

//    private static final String VALID_EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String NIGERIA_PHONE_NUMBER_PATTERN = "^(?!.*[^0-9+].*$)\\+?(234|0)[7-9][01][0-9]{8}$";

    public ValidatorException(String message) {
        super(message);
    }

    public static void validateCompanyName(String companyName){
        String pattern = ".*[@#$%,!?/\\\\].*";
        if(companyName == null || companyName.isEmpty() || companyName.trim().isEmpty() || companyName.matches(pattern)){
            throw new ValidatorException("The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.");
        }
    }
    public static void validateEmail(String email){
        EmailValidator emailValidator = EmailValidator.getInstance(false, true);
        if(email == null || email.isEmpty() || email.trim().isEmpty() || !emailValidator.isValid(email.trim())){
            throw new ValidatorException("Invalid email address.");
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.trim().isEmpty() || !phoneNumber.matches(NIGERIA_PHONE_NUMBER_PATTERN)){
            throw new ValidatorException("Invalid phone number. Please try again");
        }
    }
}
