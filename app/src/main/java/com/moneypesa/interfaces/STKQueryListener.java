package com.moneypesa.interfaces;
import com.moneypesa.api.response.STKPushResponse;

public interface STKQueryListener {

    void onResponse(STKPushResponse stkPushResponse);

    void onError(Throwable throwable);
}
