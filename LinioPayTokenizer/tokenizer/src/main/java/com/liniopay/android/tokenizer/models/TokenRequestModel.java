package com.liniopay.android.tokenizer.models;

/**
 * Created by egtej on 26/12/2017.
 */

public class TokenRequestModel {

    private TokenModel token;
    private String tokenization_key;

    public TokenRequestModel(TokenModel token, String tokenization_key) {
        this.token = token;
        this.tokenization_key = tokenization_key;
    }

    public TokenModel getToken() {
        return token;
    }

    public String getTokenization_key() {
        return tokenization_key;
    }
}
