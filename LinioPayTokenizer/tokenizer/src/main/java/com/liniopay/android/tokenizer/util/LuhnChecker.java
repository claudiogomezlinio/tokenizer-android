package com.liniopay.android.tokenizer.util;

/**
 * Created by daniel on 7/17/17.
 */

public class LuhnChecker {

    public static boolean check(String creditCardNumber) {
        if(creditCardNumber == null || creditCardNumber.trim().isEmpty()) {
            return false;
        }

        String sanitizedNumber = creditCardNumber.trim().replace("-", "").replace(" ", "");
        int[] digits = new int[sanitizedNumber.length()];
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {

            // get digits in reverse order
            int digit = digits[length - i - 1];

            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }

        return sum % 10 == 0;
    }
}
