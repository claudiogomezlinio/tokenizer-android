package com.liniopay.android.tokenizer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TokenizerTest {

    private Tokenizer tokenizer = null;

    @Before
    public void setUp() {
        tokenizer = new Tokenizer("test_20e3ec8c5ce9bb5adced9b8abe297929684", new APIListener());
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
            Tokenizer invalidTokenizer = new Tokenizer("asdfasdf", new APIListener());
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
    public void invalidStreet1() {
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
    public void validStreet1() {
        ValidationResult result1 = tokenizer.validateAddressStreet1("Main Street 123");
        Assert.assertTrue(result1.isValid());
    }
}
