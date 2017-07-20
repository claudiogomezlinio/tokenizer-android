package com.liniopay.android.tokenizer.util;

import android.util.Log;

import com.liniopay.android.tokenizer.Constants;
import com.liniopay.android.tokenizer.RequestData;

import java.util.HashMap;

/**
 * Created by daniel on 7/18/17.
 */

public class APIRequestCreator {

    public static HashMap<Object, Object> createAPIRequest(String apiKey, RequestData requestData, boolean oneTime) {
        // convert to json
        HashMap<Object, Object> chargeCardMap = new HashMap<>();
        chargeCardMap.putAll(requestData.getFormValues());

        if(requestData.getAddress() != null) {
            chargeCardMap.put("address", requestData.getAddress());
        }

        // paymenet method element
        HashMap<Object, Object> paymentMethodMap = new HashMap<>(); // payment method
        paymentMethodMap.put("charge_card", chargeCardMap);

        // token element
        HashMap<Object, Object> tokenMap = new HashMap<>(); // root object
        tokenMap.put("one_time", Boolean.valueOf(oneTime));
        tokenMap.put("payment_method", paymentMethodMap);

        // root element
        HashMap<Object, Object> rootMap = new HashMap<>(); // root object
        rootMap.put("tokenization_key", apiKey);
        rootMap.put("token", tokenMap);

        return rootMap;
    }
}
