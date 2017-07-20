package com.liniopay.android.tokenizer;

import java.util.HashMap;

/**
 * Created by daniel on 7/18/17.
 */

public class RequestData {
    HashMap<String, String> formValues;
    HashMap<String, String> address;

    public RequestData(HashMap<String, String> formValues, HashMap<String, String> address) {
        this.formValues = formValues;
        this.address = address;
    }

    public HashMap<String, String> getFormValues() {
        return formValues;
    }

    public void setFormValues(HashMap<String, String> formValues) {
        this.formValues = formValues;
    }

    public HashMap<String, String> getAddress() {
        return address;
    }

    public void setAddress(HashMap<String, String> address) {
        this.address = address;
    }
}
