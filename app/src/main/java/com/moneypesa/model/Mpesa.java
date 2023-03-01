package com.moneypesa.model;

import android.util.Base64;

import com.moneypesa.Mode;
import com.moneypesa.api.RetroClient;
import com.moneypesa.api.response.STKPushResponse;
import com.moneypesa.interfaces.STKListener;
import com.moneypesa.interfaces.STKQueryListener;
import com.moneypesa.interfaces.TokenListener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Mpesa {
    private static final String TAG = Mpesa.class.getSimpleName();
    private final CompositeSubscription mCompositeSubscription;
    private final String consumerKey;
    private final String consumerSecret;
    private final Mode mode;

    public Mpesa(String consumerKey, String consumerSecret, Mode mode) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.mode = mode;
        this.mCompositeSubscription = new CompositeSubscription();
    }

    public void getToken(final TokenListener tokenListener) throws UnsupportedEncodingException {
        if (tokenListener == null) {
            throw new RuntimeException("Activity must implement TokenListener");
        }

        mCompositeSubscription.add(RetroClient.getApiService(mode)
                .generateAccessToken(getAuth())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Token>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        tokenListener.OnTokenError(e);
                    }

                    @Override
                    public void onNext(Token token) {
                        tokenListener.TokenListener(token);
                    }
                }));
    }

    private String getAuth() {
        if (consumerKey.isEmpty()) {
            throw new RuntimeException("Consumer key cannot be empty");
        }

        if (consumerSecret.isEmpty()) {
            throw new RuntimeException("Consumer secret cannot be empty");
        }

        String consumerKeySecret = consumerKey + ":" + consumerSecret;
        byte[] bytes = consumerKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public void startStkPush(Token token, STKPush stkPush, final STKListener stkListener) {

        if (token == null) {
            throw new RuntimeException("Token cannot be null");
        }

        if (stkPush == null) {
            throw new RuntimeException("STKPush cannot be null");
        }

        if (stkListener == null) {
            throw new RuntimeException("Activity must implement TokenListener");
        }

        if (stkPush.getCallBackURL() == null) {
            throw new RuntimeException("Callback URL cannot be null");
        }

        if (stkPush.getCallBackURL().isEmpty()) {
            throw new RuntimeException("Callback URL is required");
        }

        if (stkPush.getPassword() == null) {
            throw new RuntimeException("Password can not be null");
        }

        if (stkPush.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        mCompositeSubscription.add(RetroClient.getApiService(mode).stkPush(getAuthorization(token.getAccessToken()), stkPush)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<STKPushResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        stkListener.onError(throwable);
                    }

                    @Override
                    public void onNext(STKPushResponse stkPushResponse) {
                        stkListener.onResponse(stkPushResponse);
                    }
                }));
    }

    private String getAuthorization(String accessToken) {
        return "Bearer " + accessToken;
    }

    public void stkPushQuery(Token token, STKQuery stkQuery, final STKQueryListener stkQueryListener) {

        if (token == null) {
            throw new RuntimeException("Token cannot be null");
        }
        if (stkQuery == null) {
            throw new RuntimeException("STKQuery cannot be null");
        }
        if (stkQueryListener == null) {
            throw new RuntimeException("Activity must implement STKQueryListener");
        }

        mCompositeSubscription.add(RetroClient.getApiService(mode)
                .stkPushQuery(getAuthorization(token.getAccessToken()), stkQuery)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<STKPushResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stkQueryListener.onError(e);
                    }

                    @Override
                    public void onNext(STKPushResponse stkPushResponse) {
                        stkQueryListener.onResponse(stkPushResponse);
                    }
                }));
    }
}
