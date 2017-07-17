package com.liniopay.android.tokenizer;

/**
 * Created by daniel on 7/17/17.
 */

public class ValidationResult {

    private boolean valid = false;
    private Error error = null;

    public ValidationResult(boolean valid, Error error) {
        this.valid = valid;
        this.error = error;
    }

    public boolean isValid() {
        return valid;
    }

    public Error getError() {
        return error;
    }
}
