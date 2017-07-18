package com.liniopay.android.tokenizer;

import android.util.Log;

import com.liniopay.android.tokenizer.util.LuhnChecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public ValidationResult validateCVC(String cvcNumber, String cardNumber) {
        // Field is optional
        if (cvcNumber != null && cardNumber != null) {
            String trimmedCVCNumber = cvcNumber.trim();
            String trimmedCCNumber = cardNumber.trim();
            int validCVCNumberLength = Constants.CHAR_LENGTH_CVC;

            if(trimmedCCNumber.isEmpty()) {
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_CARD_NUMBER,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_CARD_NUMBER));
            }

            if(trimmedCVCNumber.isEmpty()) {
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_CVC,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_CVC));
            }

            // If card Type Amex expect 4 digits CVC number
            String cardRegex = "^3[47]\\d+";
            Log.d(TAG, "Matching " + cardRegex + " against " + trimmedCCNumber);

            if(Pattern.compile(cardRegex).matcher(trimmedCCNumber).matches()) {
                validCVCNumberLength = Constants.CHAR_LENGTH_CVC_AMEX;
            }
            else {
                Log.d(TAG, "Did not match");
            }

            // validate cvc
            String regex = String.format("\\d{%d}", validCVCNumberLength);
            Log.d(TAG, "Validating with regex " + regex +  " number " + trimmedCVCNumber);

            if(!Pattern.compile(regex).matcher(trimmedCVCNumber).matches()) {
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_CVC,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_CVC));
            }
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateExpirationDate(String month, String year) {
        if(month == null || month.isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_MONTH,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_MONTH));
        }

        if(year == null || year.isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_YEAR,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_YEAR));
        }

        month = month.trim();
        year = year.trim();

        if(!Pattern.compile("^\\d{1,2}$").matcher(month).matches()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_MONTH,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_MONTH + ": (" + month + ")"));
        }

        if(!Pattern.compile("^\\d{4}$").matcher(year).matches()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_YEAR,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_YEAR + ": (" + year + ")"));
        }

        // now check validity of the date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat.setLenient(false);

        try {
            int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; //this crap is 0-based
            int todayYear = Calendar.getInstance().get(Calendar.YEAR);
            Date expiration = simpleDateFormat.parse(dayOfMonth + "-" + month + "-" + year);
            Date todayAtMidnight = simpleDateFormat.parse(dayOfMonth + "-" + todayMonth + "-" + todayYear);

            Log.d(TAG, "expiration is " + expiration + ", today is " + todayAtMidnight);

            if(expiration.before(todayAtMidnight)) {
                Log.e(TAG, "Date " + expiration + " is before " + todayAtMidnight);
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_EXPIRATION,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_EXPIRATION));
            }
        }
        catch(ParseException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_EXPIRATION,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_EXPIRATION));
        }

        return new ValidationResult(true, null);
    }

/*
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
