package com.moneypesa.api.response;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class STKPushResponse {

    @SerializedName("MerchantRequestID")
    @Expose
    private String merchantRequestID;
    @SerializedName("CheckoutRequestID")
    @Expose
    private String checkoutRequestID;
    @SerializedName("ResultCode")
    @Expose
    private String resultCode;
    @SerializedName("ResponseDescription")
    @Expose
    private String responseDescription;
    @SerializedName("CustomerMessage")
    @Expose
    private String customerMessage;
    @SerializedName("ResultDesc")
    @Expose
    private String resultDesc;
    @SerializedName("CallbackMetadata")
    @Expose
    private CallbackMetadata callbackMetadata;
    @SerializedName("Status")
    @Expose
    private String status = "PROCESSING";

    public String getMerchantRequestID() {
        return merchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        this.merchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return checkoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        this.checkoutRequestID = checkoutRequestID;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public CallbackMetadata getCallbackMetadata() {
        return callbackMetadata;
    }

    public void setCallbackMetadata(CallbackMetadata callbackMetadata) {
        this.callbackMetadata = callbackMetadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "STKPushResponse{" +
                "merchantRequestID='" + merchantRequestID + '\'' +
                ", checkoutRequestID='" + checkoutRequestID + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", customerMessage='" + customerMessage + '\'' +
                ", resultDesc='" + resultDesc + '\'' +
                ", callbackMetadata=" + callbackMetadata +
                ", status='" + status + '\'' +
                '}';
    }

    public String toJson(STKPushResponse stkPushResponse) {
        Gson gson = new Gson();
        return gson.toJson(stkPushResponse);
    }
}
