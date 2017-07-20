package com.liniopay.android.tokenizer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by daniel on 7/17/17.
 */

public interface APIResponseHandler {

    public void onValidationFailure(ArrayList<Error> errorArrayList);
    public void onRequestFailure(Exception e);
    public void onRequestSuccess(Map<Object, Object> responseData);
}
