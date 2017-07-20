package com.liniopay.android.tokenizer;

/**
 * Created by daniel on 7/17/17.
 */

public class Error {

    private int code;
    private String domain;
    private String message;

    public Error(int code, String domain, String message) {
        this.code = code;
        this.domain = domain;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getDomain() {
        return domain;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", domain='" + domain + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
