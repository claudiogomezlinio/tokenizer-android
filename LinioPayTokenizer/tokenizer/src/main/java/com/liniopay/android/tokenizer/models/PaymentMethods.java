package com.liniopay.android.tokenizer.models;

import java.util.HashMap;

/**
 * Created by egtej on 26/12/2017.
 */

public class PaymentMethods {

    public HashMap<Object, Object> charge_card;

    public PaymentMethods(HashMap<Object, Object> charge_card) {
        this.charge_card = charge_card;
    }
}
