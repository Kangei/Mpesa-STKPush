package com.moneypesa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moneypesa.api.response.STKPushResponse;
import com.moneypesa.interfaces.STKListener;
import com.moneypesa.interfaces.TokenListener;
import com.moneypesa.model.Mpesa;
import com.moneypesa.model.STKPush;
import com.moneypesa.model.Token;
import com.moneypesa.model.Transaction;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements TokenListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private EditText phoneET, amountET;
    private SweetAlertDialog sweetAlertDialog;
    private Mpesa mpesa;
    private String phone_number;
    private String amount;
    private FirebaseDatabase database;
    private DatabaseReference mpesaRef;
    private STKPushResponse stkPushResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();

        phoneET = findViewById(R.id.phoneET);
        amountET = findViewById(R.id.amountET);

        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);
    }

    public void startMpesa(View view) {
        this.stkPushResponse = null;

        phone_number = phoneET.getText().toString();
        amount = amountET.getText().toString();

        if (phone_number.isEmpty()) {
            Toast.makeText(MainActivity.this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.isEmpty()) {
            Toast.makeText(MainActivity.this, "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Connecting to mpesa");
            sweetAlertDialog.setContentText("Please wait...");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            mpesa.getToken(this);
        } catch (UnsupportedEncodingException e) {
            Timber.tag(TAG).e("UnsupportedEncodingException: %s", e.getLocalizedMessage());
        }
    }

    @Override
    public void TokenListener(Token token) {
        STKPush stkPush = new STKPush();
        stkPush.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
        stkPush.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
        stkPush.setTimestamp(STKPush.getTimestamp());
        stkPush.setTransactionType(Transaction.CUSTOMER_PAY_BILL_ONLINE);
        stkPush.setAmount(amount);
        stkPush.setPartyA(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setPartyB(Config.PARTYB);
        stkPush.setPhoneNumber(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setCallBackURL(Config.CALLBACKURL);
        stkPush.setAccountReference("test");
        stkPush.setTransactionDesc("some description");

        mpesa.startStkPush(token, stkPush, new STKListener() {
            @Override
            public void onResponse(STKPushResponse response) {
                stkPushResponse = response;
                Timber.tag(TAG).e("onResponse: %s", stkPushResponse.toJson(stkPushResponse));
                String message = "Please enter your pin to complete transaction";
                sweetAlertDialog.setTitleText("Transaction started");
                sweetAlertDialog.setContentText(message);
                saveToFirebase();
            }

            @Override
            public void onError(Throwable throwable) {
                Timber.tag(TAG).e("stk onError: %s", throwable.getMessage());
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Error");
                sweetAlertDialog.setContentText(throwable.getMessage());
            }
        });
    }

    private void saveToFirebase() {
        mpesaRef = database.getReference("mpesa/stk/" + stkPushResponse.getMerchantRequestID());
        mpesaRef.setValue(stkPushResponse);
        registerMpesaListener();
    }

    private void registerMpesaListener() {
        mpesaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stkPushResponse = snapshot.getValue(STKPushResponse.class);
                if (stkPushResponse != null) {
                    switch (stkPushResponse.getStatus()) {
                        case "SUCCESS":
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog.setTitleText("Success");
                            sweetAlertDialog.setContentText("Payment successful");
                            break;

                        case "FAILED":
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setTitleText("Error");
                            sweetAlertDialog.setContentText(stkPushResponse.getResultDesc());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Timber.e("Error :%s", error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnTokenError(Throwable throwable) {
        Timber.tag(TAG).e("mpesa Error: %s", throwable.getMessage());
        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error");
        sweetAlertDialog.setContentText(throwable.getMessage());
    }
}
