package com.moneypesa.interfaces;

import com.moneypesa.api.response.STKPushResponse;

public interface STKListener {

    void onResponse(STKPushResponse stkPushResponse);

    void onError(Throwable throwable);
}
