package com.richards.jonathan.foodhygieneratingsapp.model.network.objects;

import java.io.IOException;

/**
 * Created by jonathan on 06/12/2017.
 */

public class NoNetworkException extends IOException {

    public static final String NO_NETWORK_AVAILABLE = "No network available, Please check your Wifi or Data connection";

    @Override
    public String getMessage() {
        return NO_NETWORK_AVAILABLE;
    }
}
