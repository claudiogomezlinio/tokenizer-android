package com.liniopay.android.tokenizer;

import android.util.Log;

import com.liniopay.android.tokenizer.util.LuhnChecker;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daniel on 7/17/17.
 */

public class Tokenizer {

    public static final String TAG = Tokenizer.class.getSimpleName();

    /* Instance fields */
    private String apiKey = null;
    private APIListener listener = null;

    /**
     * Tokenizer constructor.
     *
     * @param apiKey API key retrieved from Linio Pay console
     * @param listener Listener for the results of the API calls
     */
    public Tokenizer(String apiKey, APIListener listener) {
        //validate arguments
        if(listener == null) throw new IllegalArgumentException("listener cannot be null");
        ValidationResult result = this.validateKey(apiKey);

        if(!result.isValid()) {
            throw new IllegalArgumentException(result.getError().getMessage());
        }

        //save values
        this.apiKey = apiKey;
        this.listener = listener;
    }

    /**
     * Requests a token from LinioPay for a given credit card.
     *
     * @param values Credit card data.
     * @param oneTime true for a one-time token, false for a multi-use token.
     */
    public void requestToken(Map<String, String> values, boolean oneTime) {

    }

    public ValidationResult validateKey(String key) {
        if(key == null || key.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_KEY,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_KEY));
        }

        //validate key format
        String regex = String.format("^\\w{%d}$", Constants.CHAR_LENGTH_KEY);
        Pattern pattern = Pattern.compile(regex);

        Log.d(TAG, "Validating key " + key);

        Matcher matcher = pattern.matcher(key);
        if(matcher.matches()) {
            return new ValidationResult(true, null);
        }
        else {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_KEY,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_KEY));
        }
    }

    public ValidationResult validateName(String name, Constants.NameType nameType) {
        String nameErrorType = null;

        switch(nameType) {
            case CreditCardHolderName:
                nameErrorType = Constants.ERROR_DESC_REQUIRED_NAME;
                break;
            case AddressFirstName:
                nameErrorType = Constants.ERROR_DESC_REQUIRED_ADDRESS_FIRST_NAME;
                break;
            case AddressLastName:
                nameErrorType = Constants.ERROR_DESC_REQUIRED_ADDRESS_LAST_NAME;
                break;
        }

        if(name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_NAME,
                    Constants.ERROR_DOMAIN, nameErrorType));
        }

        if(name.length() < Constants.MIN_CHAR_NAME) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MIN_LIMIT_NAME,
                    Constants.ERROR_DOMAIN, String.format(Constants.ERROR_DESC_CHAR_MIN_LIMIT_NAME, Constants.MIN_CHAR_NAME)));
        }

        if(name.length() > Constants.MAX_CHAR_NAME) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MAX_LIMIT_NAME,
                    Constants.ERROR_DOMAIN, String.format(Constants.ERROR_DESC_CHAR_MAX_LIMIT_NAME, Constants.MAX_CHAR_NAME)));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateCreditCardNumber(String cardNumber) {
        if(cardNumber == null || cardNumber.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_CARD_NUMBER,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_CARD_NUMBER));
        }

        //luhn check
        boolean valid = LuhnChecker.check(cardNumber);

        if(!valid) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_CARD_NUMBER,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_CARD_NUMBER));
        }

        return new ValidationResult(true, null);
    }

/*
- (BOOL)validateCVC:(NSString *)cvcNumber card:(NSString *)cardNumber error:(NSError **)outError;
- (BOOL)validateExpDate:(NSString *)monthValue year:(NSString *)yearValue error:(NSError **)outError;
- (BOOL)validateAddressStreet1:(NSString *)addressStreet1 error:(NSError **)outError;
- (BOOL)validateOptionalAddressLine:(NSString *)addressLine type:(AddressLineType)lineType error:(NSError **)outError;
- (BOOL)validateAddressCity:(NSString *)city error:(NSError **)outError;
- (BOOL)validateAddressState:(NSString *)state error:(NSError **)outError;
- (BOOL)validateAddressCountry:(NSString *)country error:(NSError **)outError;
- (BOOL)validateAddressPostalCode:(NSString *)postalCode error:(NSError **)outError;
- (BOOL)validateEmail:(NSString*)email error:(NSError **)outError;
- (void)requestToken:(NSDictionary *)formValues oneTime:(BOOL)oneTime completion:(void (^)(NSDictionary* data, NSError* error))c
*/
}
