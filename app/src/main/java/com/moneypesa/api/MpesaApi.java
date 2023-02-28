package com.moneypesa.api;

import com.moneypesa.api.response.STKPushResponse;
import com.moneypesa.model.STKPush;
import com.moneypesa.model.STKQuery;
import com.moneypesa.model.Token;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
public interface MpesaApi {

    @Headers({"Cache-Control: max-age=640000", "cache-control: no-cache"})
    @GET("oauth/v1/generate?grant_type=client_credentials")
    Observable<Token> generateAccessToken(@Header("Authorization") String authorization);

    @Headers("content-type: application/json")
    @POST("mpesa/stkpush/v1/processrequest")
    Observable<STKPushResponse> stkPush(@Header("Authorization") String authorization, @Body STKPush stkPush);

    @Headers("content-type: application/json")
    @POST("mpesa/stkpush/v1/query")
    Observable<STKPushResponse> stkPushQuery(@Header("Authorization") String authorization, @Body STKQuery stkQuery);

}
