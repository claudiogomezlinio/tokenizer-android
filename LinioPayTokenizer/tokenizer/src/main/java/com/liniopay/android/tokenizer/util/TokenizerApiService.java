package com.liniopay.android.tokenizer.util;

import com.liniopay.android.tokenizer.models.TokenRequestModel;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by egtej on 26/12/2017.
 */

public interface TokenizerApiService {

    @POST("token")
    Call<HashMap<Object, Object>> getToken(@Body TokenRequestModel requestModel);

}
