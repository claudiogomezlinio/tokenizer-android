package com.liniopay.android.tokenizer;

/**
 * Created by daniel on 7/17/17.
 */

public class Constants {

    /* Public constants for credit card fields */

    public static enum NameType {
        CreditCardHolderName,
        AddressFirstName,
        AddressLastName
    }

    public static enum AddressLineType {
        AddressStreet2,
        AddressStreet3,
        County,
        Phone
    }

    public static final String FORM_DICT_KEY_NAME = "cardholder";
    public static final String FORM_DICT_KEY_NUMBER = "number";
    public static final String FORM_DICT_KEY_CVC = "cvc";
    public static final String FORM_DICT_KEY_MONTH = "expiration_month";
    public static final String FORM_DICT_KEY_YEAR = "expiration_year";
    public static final String FORM_DICT_KEY_ADDRESS = "address";
    public static final String FORM_DICT_KEY_ADDRESS_FIRST_NAME = "first_name";
    public static final String FORM_DICT_KEY_ADDRESS_LAST_NAME = "last_name";
    public static final String FORM_DICT_KEY_STREET_1 = "street1";
    public static final String FORM_DICT_KEY_STREET_2 = "street2";
    public static final String FORM_DICT_KEY_STREET_3 = "street3";
    public static final String FORM_DICT_KEY_PHONE = "phone_number";
    public static final String FORM_DICT_KEY_CITY = "city";
    public static final String FORM_DICT_KEY_STATE = "state";
    public static final String FORM_DICT_KEY_COUNTRY = "country_code";
    public static final String FORM_DICT_KEY_COUNTY = "county";
    public static final String FORM_DICT_KEY_POSTAL_CODE = "postal_code";
    public static final String FORM_DICT_KEY_EMAIL = "email";

    /* Internal constants of the SDK */
    static final String LPTS_API_PATH = "https://vault.liniopay.com/token";
    static final String ERROR_DOMAIN = "com.LinioPay.Tokenizer.ErrorDomain";
    static final int CHAR_LENGTH_KEY = 40;
    static final int CHAR_LENGTH_CVC = 3;
    static final int CHAR_LENGTH_CVC_AMEX = 4;
    public static final int CHAR_LENGTH_COUNTRY = 2;
    public static final int MIN_CHAR_NAME = 5;
    public static final int MAX_CHAR_NAME = 60;
    public static final int MAX_CHAR_STREET_1 = 255;
    public static final int MAX_CHAR_STREET_2 = 255;
    public static final int MAX_CHAR_STREET_3 = 255;
    public static final int MAX_CHAR_CITY = 255;
    public static final int MAX_CHAR_STATE = 120;
    public static final int MAX_CHAR_COUNTY = 255;
    public static final int MAX_CHAR_PHONE = 50;
    public static final int MAX_CHAR_POSTAL_CODE = 20;
    public static final int ERROR_CODE_REQUIRED_KEY = 100;
    public static final int ERROR_CODE_REQUIRED_NAME = 105;
    public static final int ERROR_CODE_REQUIRED_CARD_NUMBER = 110;
    public static final int ERROR_CODE_REQUIRED_MONTH = 130;
    public static final int ERROR_CODE_REQUIRED_YEAR = 140;
    public static final int ERROR_CODE_REQUIRED_STREET_1 = 160;
    public static final int ERROR_CODE_REQUIRED_CITY = 170;
    public static final int ERROR_CODE_REQUIRED_STATE = 180;
    public static final int ERROR_CODE_REQUIRED_COUNTRY = 190;
    public static final int ERROR_CODE_REQUIRED_POSTAL_CODE = 195;
    public static final int ERROR_CODE_INVALID_KEY = 400;
    public static final int ERROR_CODE_INVALID_CARD_NUMBER = 410;
    public static final int ERROR_CODE_INVALID_CVC = 420;
    public static final int ERROR_CODE_INVALID_MONTH = 430;
    public static final int ERROR_CODE_INVALID_YEAR = 440;
    public static final int ERROR_CODE_INVALID_EXPIRATION = 450;
    public static final int ERROR_CODE_INVALID_COUNTRY = 490;
    public static final int ERROR_CODE_INVALID_POSTAL_CODE = 495;
    public static final int ERROR_CODE_INVALID_EMAIL = 496;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_NAME = 505;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_STREET_1 = 560;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_STREET_2 = 565;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_STREET_3 = 566;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_PHONE = 567;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_CITY = 570;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_STATE = 580;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_COUNTY = 581;
    public static final int ERROR_CODE_CHAR_MAX_LIMIT_POSTAL_CODE = 585;
    public static final int ERROR_CODE_CHAR_MIN_LIMIT_NAME = 605;
    public static final String ERROR_DESC_REQUIRED_KEY = "Tokenization Key is Required";
    public static final String ERROR_DESC_REQUIRED_NAME = "Card Holder Name is Required";
    public static final String ERROR_DESC_REQUIRED_CARD_NUMBER = "Credit Card Number is Required";
    public static final String ERROR_DESC_REQUIRED_MONTH = "Expiration Month is Required";
    public static final String ERROR_DESC_REQUIRED_YEAR = "Expiration Year is Required";
    public static final String ERROR_DESC_REQUIRED_ADDRESS_FIRST_NAME = "Address First Name is Required";
    public static final String ERROR_DESC_REQUIRED_ADDRESS_LAST_NAME = "Address Last Name is Required";
    public static final String ERROR_DESC_REQUIRED_STREET_1 = "Address Street line 1 is Required";
    public static final String ERROR_DESC_REQUIRED_CITY = "Address City is Required";
    public static final String ERROR_DESC_REQUIRED_STATE = "Address State is Required";
    public static final String ERROR_DESC_REQUIRED_COUNTRY = "Address Country is Required";
    public static final String ERROR_DESC_REQUIRED_POSTAL_CODE = "Address Postal Code is Required";
    public static final String ERROR_DESC_INVALID_KEY = "Invalid Tokenization Key - Should Contain 40 Characters";
    public static final String ERROR_DESC_INVALID_CARD_NUMBER = "Invalid Credit Card Number";
    public static final String ERROR_DESC_INVALID_CVC = "Invalid CVC Number";
    public static final String ERROR_DESC_INVALID_MONTH = "Invalid Expiration Month - Should Contain 2 Numeric Characters";
    public static final String ERROR_DESC_INVALID_YEAR = "Invalid Expiration Year - Should Contain 4 Numeric Characters";
    public static final String ERROR_DESC_INVALID_EXPIRATION = "Invalid Expiration date - Should Containt Current or Future Date";
    public static final String ERROR_DESC_INVALID_COUNTRY = "Invalid Country - Should Contain 2 Characters Country Code";
    public static final String ERROR_DESC_INVALID_POSTAL_CODE = "Invalid Postal Code - Should Contain Only Numeric Characters";
    public static final String ERROR_DESC_INVALID_EMAIL = "Invalid E-mail Address: %";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_NAME = "Card Holder Name Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_STREET_1 = "Address Street line 1 Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_STREET_2 = "Address Street line 2 Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_STREET_3 = "Address Street line 3 Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_PHONE = "Phone Number Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_CITY = "Address City Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_STATE = "Address State Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_COUNTY = "County Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MAX_LIMIT_POSTAL_CODE = "Address Postal Code Should Have Less Than %d Characters";
    public static final String ERROR_DESC_CHAR_MIN_LIMIT_NAME = "Card Holder Name Should Have More Than %d Characters";
}
