package com.liniopay.android.tokenizer;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.liniopay.android.tokenizer.util.APIRequestCreator;
import com.liniopay.android.tokenizer.util.LuhnChecker;
import com.liniopay.android.tokenizer.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daniel on 7/17/17.
 */

public class Tokenizer {

    public static final String TAG = Tokenizer.class.getSimpleName();

    /* Instance fields */
    private String apiKey = null;
    private Context context = null;

    /**
     * Tokenizer constructor.
     *
     * @param apiKey API key retrieved from Linio Pay console
     * @param listener Listener for the results of the API calls
     */
    public Tokenizer(String apiKey, Context context) {
        if(context == null) {
            Log.e(TAG, "Context is null, can't construct class");
            throw new IllegalArgumentException("Null context not allowed");
        }
        else {
            this.context = context;
        }

        //validate arguments
        ValidationResult result = this.validateKey(apiKey);

        if(!result.isValid()) {
            throw new IllegalArgumentException(result.getError().getMessage());
        }

        //save values
        this.apiKey = apiKey;
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

            if(Pattern.matches(cardRegex, trimmedCCNumber)) {
                validCVCNumberLength = Constants.CHAR_LENGTH_CVC_AMEX;
            }
            else {
                Log.d(TAG, "Did not match");
            }

            // validate cvc
            String regex = String.format("\\d{%d}", validCVCNumberLength);
            Log.d(TAG, "Validating with regex " + regex +  " number " + trimmedCVCNumber);

            if(!Pattern.matches(regex, trimmedCVCNumber)) {
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_CVC,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_CVC));
            }
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateExpirationDate(String month, String year) {
        if(month == null || month.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_MONTH,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_MONTH));
        }

        if(year == null || year.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_YEAR,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_YEAR));
        }

        month = month.trim();
        year = year.trim();

        if(!Pattern.matches("^\\d{1,2}$", month)) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_MONTH,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_MONTH + ": (" + month + ")"));
        }

        if(!Pattern.matches("^\\d{4}$", year)) {
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

    public ValidationResult validateAddressStreet1(String street1) {
        if(street1 == null || street1.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_STREET_1,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_STREET_1));
        }

        String trimmedStreet1 = street1.trim();

        if(trimmedStreet1.length() > Constants.MAX_CHAR_STREET_1) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MAX_LIMIT_STREET_1,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_CHAR_MAX_LIMIT_STREET_1));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateOptionalAddressLine(String addressLine, Constants.AddressLineType lineType) {
        // field is optional
        if(addressLine != null && !addressLine.trim().isEmpty()) {
            int maxCharacters = 0;
            int errorCode = 0;
            String errorString = "";

            switch(lineType) {
                case AddressStreet2:
                    maxCharacters = Constants.MAX_CHAR_STREET_2;
                    errorCode = Constants.ERROR_CODE_CHAR_MAX_LIMIT_STREET_2;
                    errorString = Constants.ERROR_DESC_CHAR_MAX_LIMIT_STREET_2;
                    break;
                case AddressStreet3:
                    maxCharacters = Constants.MAX_CHAR_STREET_3;
                    errorCode = Constants.ERROR_CODE_CHAR_MAX_LIMIT_STREET_3;
                    errorString = Constants.ERROR_DESC_CHAR_MAX_LIMIT_STREET_3;
                    break;
                case County:
                    maxCharacters = Constants.MAX_CHAR_COUNTY;
                    errorCode = Constants.ERROR_CODE_CHAR_MAX_LIMIT_COUNTY;
                    errorString = Constants.ERROR_DESC_CHAR_MAX_LIMIT_COUNTY;
                    break;
                case Phone:
                    maxCharacters = Constants.MAX_CHAR_PHONE;
                    errorCode = Constants.ERROR_CODE_CHAR_MAX_LIMIT_PHONE;
                    errorString = Constants.ERROR_DESC_CHAR_MAX_LIMIT_PHONE;
                    break;
            }

            if (addressLine.length() > maxCharacters) {
                return new ValidationResult(false, new Error(errorCode,
                        Constants.ERROR_DOMAIN, errorString));
            }
        }
        return new ValidationResult(true, null);
    }

    public ValidationResult validateAddressCity(String city) {
        if(city == null || city.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_CITY,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_CITY));
        }

        if(city.length() > Constants.MAX_CHAR_CITY) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MAX_LIMIT_CITY,
                    Constants.ERROR_DOMAIN, String.format(Constants.ERROR_DESC_CHAR_MAX_LIMIT_CITY, Constants.MAX_CHAR_CITY)));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateAddressState(String state) {
        if(state == null || state.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_STATE,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_STATE));
        }

        if(state.length() > Constants.MAX_CHAR_STATE) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MAX_LIMIT_STATE,
                    Constants.ERROR_DOMAIN, String.format(Constants.ERROR_DESC_CHAR_MAX_LIMIT_STATE, Constants.MAX_CHAR_STATE)));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateAddressCountry(String country) {
        if(country == null || country.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_COUNTRY,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_COUNTRY));
        }

        if(!Pattern.matches(String.format("^[\\sA-Za-z]{%d}$", Constants.CHAR_LENGTH_COUNTRY), country)) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_COUNTRY,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_COUNTRY));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateAddressPostalCode(String postalCode) {
        if(postalCode == null || postalCode.trim().isEmpty()) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_REQUIRED_POSTAL_CODE,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_REQUIRED_POSTAL_CODE));
        }

        String trimmedPostalCode = postalCode.trim();

        if(trimmedPostalCode.length() > Constants.MAX_CHAR_POSTAL_CODE) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_CHAR_MAX_LIMIT_POSTAL_CODE,
                    Constants.ERROR_DOMAIN, String.format(Constants.ERROR_DESC_CHAR_MAX_LIMIT_POSTAL_CODE,
                    Constants.MAX_CHAR_POSTAL_CODE)));
        }

        if(!Pattern.matches("^\\d+$", postalCode)) {
            return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_POSTAL_CODE,
                    Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_POSTAL_CODE));
        }

        return new ValidationResult(true, null);
    }

    public ValidationResult validateEmail(String email) {
        if(email != null && !email.trim().isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return new ValidationResult(false, new Error(Constants.ERROR_CODE_INVALID_EMAIL,
                        Constants.ERROR_DOMAIN, Constants.ERROR_DESC_INVALID_EMAIL));
            }
        }

        return new ValidationResult(true, null);
    }

    public void requestToken(RequestData request, boolean oneTime, final APIResponseHandler responseHandler) {
        if(request == null || request.getFormValues() == null) {
            throw new IllegalArgumentException("Form values cannot be null");
        }

        ArrayList<Error> errorArrayList = new ArrayList<>();
        ValidationResult result = null;

        // hash maps with the data
        HashMap<String, String> formValues = request.getFormValues();
        HashMap<String, String> address = request.getAddress();

        // validate name
        result = this.validateName(formValues.get(Constants.FORM_DICT_KEY_NAME),
                                    Constants.NameType.CreditCardHolderName);
        addErrorMessageIfValidationFailed(result, errorArrayList);

        // validate credit card number
        result = this.validateCreditCardNumber(formValues.get(Constants.FORM_DICT_KEY_NUMBER));
        addErrorMessageIfValidationFailed(result, errorArrayList);

        // validate expiration date
        result = this.validateExpirationDate(formValues.get(Constants.FORM_DICT_KEY_MONTH),
                                             formValues.get(Constants.FORM_DICT_KEY_YEAR));
        addErrorMessageIfValidationFailed(result, errorArrayList);

        // address is always optional so we check first
        if(address != null) {
            // first name
            result = this.validateName(address.get(Constants.FORM_DICT_KEY_ADDRESS_FIRST_NAME), Constants.NameType.AddressFirstName);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // last name
            result = this.validateName(address.get(Constants.FORM_DICT_KEY_ADDRESS_LAST_NAME), Constants.NameType.AddressLastName);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // street 1
            result = this.validateAddressStreet1(address.get(Constants.FORM_DICT_KEY_STREET_1));
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // street 2
            result = this.validateOptionalAddressLine(address.get(Constants.FORM_DICT_KEY_STREET_2), Constants.AddressLineType.AddressStreet2);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // street 3
            result = this.validateOptionalAddressLine(address.get(Constants.FORM_DICT_KEY_STREET_3), Constants.AddressLineType.AddressStreet3);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // phone
            result = this.validateOptionalAddressLine(address.get(Constants.FORM_DICT_KEY_PHONE), Constants.AddressLineType.Phone);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // county
            result = this.validateOptionalAddressLine(address.get(Constants.FORM_DICT_KEY_COUNTY), Constants.AddressLineType.County);
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // city
            result = this.validateAddressCity(address.get(Constants.FORM_DICT_KEY_CITY));
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // state
            result = this.validateAddressState(address.get(Constants.FORM_DICT_KEY_STATE));
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // country
            result = this.validateAddressCountry(address.get(Constants.FORM_DICT_KEY_COUNTRY));
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // postal code
            result = this.validateAddressPostalCode(address.get(Constants.FORM_DICT_KEY_POSTAL_CODE));
            addErrorMessageIfValidationFailed(result, errorArrayList);

            // email
            result = this.validateEmail(address.get(Constants.FORM_DICT_KEY_EMAIL));
            addErrorMessageIfValidationFailed(result, errorArrayList);
        }

        // if there are failed validations, notify handler and leave
        if(!errorArrayList.isEmpty()) {
            responseHandler.onValidationFailure(errorArrayList);
        }
        else {
            // let's make the actual request
            RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
            JSONObject jsonObject = new JSONObject(APIRequestCreator.createAPIRequest(this.apiKey, request, oneTime));

            // contact the LinioPay endpoint
            JsonObjectRequest req = new JsonObjectRequest(Constants.LPTS_API_PATH, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String jsonBody = response.toString();
                            HashMap<Object, Object> responseData = new Gson().fromJson(jsonBody, HashMap.class);
                            responseHandler.onRequestSuccess(responseData);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.getMessage());
                    responseHandler.onRequestFailure(new Exception(error.getMessage(), error.getCause()));
                }
            });

            // Add the request to the RequestQueue.
            queue.add(req);
        }
    }

    private void addErrorMessageIfValidationFailed(ValidationResult result, ArrayList<Error> errorList) {
        if(!result.isValid()) {
            errorList.add(result.getError());
        }
    }
}
