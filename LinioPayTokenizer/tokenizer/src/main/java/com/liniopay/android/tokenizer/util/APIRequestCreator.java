package com.liniopay.android.tokenizer.util;

import com.liniopay.android.tokenizer.RequestData;
import com.liniopay.android.tokenizer.models.PaymentMethods;
import com.liniopay.android.tokenizer.models.TokenModel;
import com.liniopay.android.tokenizer.models.TokenRequestModel;

import java.util.HashMap;

/**
 * Created by daniel on 7/18/17.
 */

public class APIRequestCreator {

    public static TokenRequestModel createAPIRequest(String apiKey, RequestData requestData, boolean oneTime) {
        HashMap<Object, Object> chargeCardMap = new HashMap<>();
        // Adds charge cards info
        chargeCardMap.putAll(requestData.getFormValues());
        // If has address data add to
        if (requestData.getAddress() != null) {
            chargeCardMap.put("address", requestData.getAddress());
        }
        // Return build model
        return new TokenRequestModel(new TokenModel(oneTime, new PaymentMethods(chargeCardMap)), apiKey);
    }

}
