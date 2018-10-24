package com.liniopay.android.tokenizer;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TokenizerTest {

    private static final String TAG = TokenizerTest.class.getSimpleName();
    private Tokenizer tokenizer = null;

    @Before
    public void setUp() {
        tokenizer = new Tokenizer("test_20e3ec8c5ce9bb5adced9b8abe297929684", InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
        tokenizer = null;
    }

    @Test
    public void validKeyTest() throws Exception {

    }

    @Test
    public void invalidKeyTest() throws Exception {
        try {
            Tokenizer invalidTokenizer = new Tokenizer("asdfasdf", InstrumentationRegistry.getContext());
            Assert.fail("Invalid key accepted");
        }
        catch(IllegalArgumentException iae) {
            //test passes
        }
    }

    @Test
    public void emptyNameTest() {
        ValidationResult result = tokenizer.validateName("", Constants.NameType.CreditCardHolderName);
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void smallNameTest() {
        ValidationResult result = tokenizer.validateName("a", Constants.NameType.CreditCardHolderName);
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void normalNameTest() {
        ValidationResult result = tokenizer.validateName("aasdfasfaasd sdafsdf asdf asdfsdf", Constants.NameType.CreditCardHolderName);
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void longNameTest() {
        ValidationResult result = tokenizer.validateName("aasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasf " +
                "aasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasf" +
                "aasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasfaasdfasf" +
                "aasdfasf", Constants.NameType.CreditCardHolderName);
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void emptyCCTest() {
        ValidationResult result = tokenizer.validateCreditCardNumber("");
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void invalidCCTest() {
        ValidationResult result = tokenizer.validateCreditCardNumber("");
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void validAmexCCTest() {
        ValidationResult result = tokenizer.validateCreditCardNumber("378734493671000");
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void validCCTest() {
        ValidationResult result = tokenizer.validateCreditCardNumber("4111111111111111");
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void emptyCVCTest() {
        ValidationResult result = tokenizer.validateCVC("", "");
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void invalidCVCTest() {
        ValidationResult result = tokenizer.validateCVC("1", "4111111111111111");
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void validAmexCVCTest() {
        ValidationResult result = tokenizer.validateCVC("1234", "378734493671000");
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void validCVCTest() {
        ValidationResult result = tokenizer.validateCVC("123", "4111111111111111");
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void invalidExpirationMonthTest() {
        ValidationResult result1 = tokenizer.validateExpirationDate(null, "2010");
        ValidationResult result2 = tokenizer.validateExpirationDate("", "2010");
        ValidationResult result3 = tokenizer.validateExpirationDate("13", "2010");
        ValidationResult result4 = tokenizer.validateExpirationDate("0", "2010");

        Assert.assertFalse(result1.isValid() || result2.isValid()
                || result3.isValid() || result4.isValid());
    }

    @Test
    public void invalidExpirationYearTest() {
        ValidationResult result1 = tokenizer.validateExpirationDate("01", "");
        ValidationResult result2 = tokenizer.validateExpirationDate("01", null);
        ValidationResult result3 = tokenizer.validateExpirationDate("01", "-305");

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void invalidExpirationDateTest() {
        ValidationResult result1 = tokenizer.validateExpirationDate("01", "2010");
        ValidationResult result2 = tokenizer.validateExpirationDate("1", "2010");
        ValidationResult result3 = tokenizer.validateExpirationDate("6", "2017");
        ValidationResult result4 = tokenizer.validateExpirationDate("13", "2017");

        Assert.assertFalse(result1.isValid() || result2.isValid()
                || result3.isValid() || result4.isValid());
    }

    @Test
    public void validExpirationDateTest() {
        ValidationResult result1 = tokenizer.validateExpirationDate("1", "2025");
        ValidationResult result2 = tokenizer.validateExpirationDate("03", "2020");
        ValidationResult result3 = tokenizer.validateExpirationDate("7", "2017");
        ValidationResult result4 = tokenizer.validateExpirationDate("8", "2027");

        Assert.assertTrue(result1.isValid() && result2.isValid()
                && result3.isValid() && result4.isValid());
    }

    @Test
    public void invalidStreet1Test() {
        ValidationResult result1 = tokenizer.validateAddressStreet1("");
        ValidationResult result2 = tokenizer.validateAddressStreet1("   ");

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Constants.MAX_CHAR_STREET_1 + 10;i++) {
            sb.append("Main St.");
        }
        ValidationResult result3 = tokenizer.validateAddressStreet1(sb.toString());

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void validStreet1Test() {
        ValidationResult result1 = tokenizer.validateAddressStreet1("Main Street 123");
        Assert.assertTrue(result1.isValid());
    }

    @Test
    public void invalidAddressLineTest() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Constants.MAX_CHAR_STREET_3 + 10;i++) {
            sb.append("Main St.");
        }

        ValidationResult result3 = tokenizer.validateOptionalAddressLine(sb.toString(), Constants.AddressLineType.AddressStreet3);

        Assert.assertFalse(result3.isValid());
    }

    @Test
    public void validOptionalAddressLineTest() {
        ValidationResult result1 = tokenizer.validateOptionalAddressLine("", Constants.AddressLineType.AddressStreet2);
        ValidationResult result2 = tokenizer.validateOptionalAddressLine("", Constants.AddressLineType.AddressStreet3);

        Assert.assertTrue(result1.isValid() && result2.isValid());
    }

    @Test
    public void invalidCityTest() {
        ValidationResult result1 = tokenizer.validateAddressCity("");
        ValidationResult result2 = tokenizer.validateAddressCity(null);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Constants.MAX_CHAR_CITY + 10;i++) {
            sb.append("Main St.");
        }
        ValidationResult result3 = tokenizer.validateAddressCity(sb.toString());

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void validCityTest() {
        ValidationResult result1 = tokenizer.validateAddressCity("Main Street 123");
        Assert.assertTrue(result1.isValid());
    }

    @Test
    public void invalidStateTest() {
        ValidationResult result1 = tokenizer.validateAddressState("");
        ValidationResult result2 = tokenizer.validateAddressState(null);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Constants.MAX_CHAR_STATE + 10;i++) {
            sb.append("Main St.");
        }
        ValidationResult result3 = tokenizer.validateAddressState(sb.toString());

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void validStateTest() {
        ValidationResult result1 = tokenizer.validateAddressState("Guadalajara");
        Assert.assertTrue(result1.isValid());
    }

    @Test
    public void invalidCountryTest() {
        ValidationResult result1 = tokenizer.validateAddressCountry("");
        ValidationResult result2 = tokenizer.validateAddressCountry(null);
        ValidationResult result3 = tokenizer.validateAddressCountry("Country123");

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void validCountryTest() {
        ValidationResult result1 = tokenizer.validateAddressCountry("US");
        Assert.assertTrue(result1.isValid());
    }

    @Test
    public void invalidPostalCodeTest() {
        ValidationResult result1 = tokenizer.validateAddressPostalCode("");
        ValidationResult result2 = tokenizer.validateAddressPostalCode(null);
        ValidationResult result3 = tokenizer.validateAddressPostalCode("Country123");

        Assert.assertFalse(result1.isValid() || result2.isValid() || result3.isValid());
    }

    @Test
    public void validPostalCodeTest() {
        ValidationResult result1 = tokenizer.validateAddressState("33180");
        Assert.assertTrue(result1.isValid());
    }

    @Test
    public void invalidEmailTest() {
        ValidationResult result1 = tokenizer.validateEmail("asfasdfasdf");
        ValidationResult result2 = tokenizer.validateEmail("dcasdfnada.123");

        Assert.assertFalse(result1.isValid() || result2.isValid());
    }

    @Test
    public void validEmailTest() {
        ValidationResult result1 = tokenizer.validateEmail(null);
        ValidationResult result2 = tokenizer.validateEmail("");
        ValidationResult result3 = tokenizer.validateEmail("test@liniopay.com");
        Assert.assertTrue(result1.isValid() && result2.isValid() && result3.isValid());
    }

    @Test
    public void emptyRequestTest() {
        HashMap<String, String> formValues = new HashMap<>();
        RequestData data = new RequestData(formValues, null);

        tokenizer.requestToken(data, true, new APIResponseHandler() {
            @Override
            public void onValidationFailure(ArrayList<Error> errorArrayList) {
                Assert.assertTrue(errorArrayList.size() == 3); // cc holder name, cc number, expiration
            }

            @Override
            public void onRequestFailure(Exception e) {

            }

            @Override
            public void onRequestSuccess(Map<Object, Object> responseData) {

            }
        });
    }

    @Test
    public void minimalRequestTest() {
        HashMap<String, String> formValues = new HashMap<>();

        // fill up request
        formValues.put(Constants.FORM_DICT_KEY_NAME, "Linio Pay");
        formValues.put(Constants.FORM_DICT_KEY_NUMBER, "4111111111111111");
        formValues.put(Constants.FORM_DICT_KEY_MONTH, "1");
        formValues.put(Constants.FORM_DICT_KEY_YEAR, "2020");

        RequestData data = new RequestData(formValues, null);

        // now wait for last year
        final Object lock = new Object();

        tokenizer.requestToken(data, true, new APIResponseHandler() {
            @Override
            public void onValidationFailure(ArrayList<Error> errorArrayList) {
                Assert.fail("There shouldn't be any failures here");
            }

            @Override
            public void onRequestFailure(Exception e) {
                Assert.fail("There shouldn't be any failures here");
            }

            @Override
            public void onRequestSuccess(Map<Object, Object> responseData) {
                Assert.assertNotNull(responseData);
                Assert.assertNotNull(responseData.get("token_id"));
                Log.d(TAG, "Unlocking thread, response " + responseData);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        try {
            synchronized (lock) {
                lock.wait();
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Test
    public void minimalRequestIncompleteAddressTest() {
        HashMap<String, String> formValues = new HashMap<>();

        // fill up request
        formValues.put(Constants.FORM_DICT_KEY_NAME, "Linio Pay");
        formValues.put(Constants.FORM_DICT_KEY_NUMBER, "4111111111111111");
        formValues.put(Constants.FORM_DICT_KEY_MONTH, "1");
        formValues.put(Constants.FORM_DICT_KEY_YEAR, "2020");

        HashMap<String, String> address = new HashMap<>();

        // fill up request
        address.put(Constants.FORM_DICT_KEY_ADDRESS_FIRST_NAME, "Linio");

        RequestData data = new RequestData(formValues, address);

        tokenizer.requestToken(data, true, new APIResponseHandler() {
            @Override
            public void onValidationFailure(ArrayList<Error> errorArrayList) {
                // first name, last name, street1, city, state, country, postal code
                Log.d(TAG, errorArrayList.toString());
                Assert.assertTrue(errorArrayList.size() == 6);
            }

            @Override
            public void onRequestFailure(Exception e) {

            }

            @Override
            public void onRequestSuccess(Map<Object, Object> responseData) {

            }
        });
    }

    @Test
    public void minimalRequestMinimalAddressTest() {
        HashMap<String, String> formValues = new HashMap<>();

        // fill up request
        formValues.put(Constants.FORM_DICT_KEY_NAME, "Linio Pay");
        formValues.put(Constants.FORM_DICT_KEY_NUMBER, "4111111111111111");
        formValues.put(Constants.FORM_DICT_KEY_MONTH, "1");
        formValues.put(Constants.FORM_DICT_KEY_YEAR, "2020");

        HashMap<String, String> address = new HashMap<>();

        // fill up request
        address.put(Constants.FORM_DICT_KEY_ADDRESS_FIRST_NAME, "Linio");
        address.put(Constants.FORM_DICT_KEY_ADDRESS_LAST_NAME, "Payer");
        address.put(Constants.FORM_DICT_KEY_STREET_1, "3400 Main Street");
        address.put(Constants.FORM_DICT_KEY_CITY, "Guadalajara");
        address.put(Constants.FORM_DICT_KEY_STATE, "Guadalajara");
        address.put(Constants.FORM_DICT_KEY_COUNTRY, "US");
        address.put(Constants.FORM_DICT_KEY_POSTAL_CODE, "33180");

        RequestData data = new RequestData(formValues, address);

        // now wait for async request
        final Object lock = new Object();

        tokenizer.requestToken(data, true, new APIResponseHandler() {
            @Override
            public void onValidationFailure(ArrayList<Error> errorArrayList) {
                Log.d(TAG, errorArrayList.toString());
                Assert.fail("There shouldn't be any failures here");
            }

            @Override
            public void onRequestFailure(Exception e) {
                Assert.fail("There shouldn't be any failures here");
            }

            @Override
            public void onRequestSuccess(Map<Object, Object> responseData) {
                Assert.assertNotNull(responseData);
                Assert.assertNotNull(responseData.get("token_id"));
                Log.d(TAG, "Unlocking thread, response " + responseData);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        try {
            synchronized (lock) {
                lock.wait();
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
