package com.moneypesa.interfaces;

import com.moneypesa.model.Token;

public interface TokenListener {

    void TokenListener(Token token);
    void OnTokenError(Throwable throwable);
}
