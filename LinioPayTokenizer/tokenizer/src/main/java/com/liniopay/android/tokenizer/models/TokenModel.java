package com.liniopay.android.tokenizer.models;

/**
 * Created by egtej on 26/12/2017.
 */

public class TokenModel {

    private boolean one_time;
    private PaymentMethods payment_method;

    public boolean isOne_time() {
        return one_time;
    }

    public TokenModel(boolean one_time, PaymentMethods payment_method) {
        this.one_time = one_time;
        this.payment_method = payment_method;
    }

    public PaymentMethods getPayment_method() {
        return payment_method;
    }
}
