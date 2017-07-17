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
}
