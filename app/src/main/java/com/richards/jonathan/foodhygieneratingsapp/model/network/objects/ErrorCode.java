package com.richards.jonathan.foodhygieneratingsapp.model.network.objects;

/**
 * Created by jonathan on 06/12/2017.
 */

public enum ErrorCode {

    ERROR_NO_CONNECTION(1),
    ERROR_OTHER(2);

    private int errorCodeValue;

    ErrorCode(int errorCodeValue){
        this.errorCodeValue = errorCodeValue;
    }

    public int getErrorCodeValue() {
        return errorCodeValue;
    }
}
