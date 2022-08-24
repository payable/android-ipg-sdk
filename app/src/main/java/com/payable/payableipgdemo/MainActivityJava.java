package com.payable.payableipgdemo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.payable.ipg.PAYableIPGClient;
import com.payable.ipg.model.IPGListener;
import com.payable.ipg.model.IPGPayment;
import com.payable.ipg.model.IPGStatusListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        PAYableIPGClient ipgClient = new PAYableIPGClient(
                "A75BCD8EF30E529A",
                "B8727C74D29E210F9A297B65690C0140",
                "https://www.sandboxmerdev.payable.lk",
                "https://i.imgur.com/l21F5us.png",
                PAYableIPGClient.Environment.SANDBOX,
                true
        );

        IPGPayment ipgPayment = new IPGPayment(
                10.00,
                "LKR",
                "Netflix",
                "Aslam",
                "Anver",
                "aslam@payable.lk",
                "0762724081",
                "Hill Street",
                "Dehiwala",
                "LK",
                "10350"
        );

        ipgClient.startPayment(this, ipgPayment, new IPGListener() {

            @Override
            public void onPaymentPageLoaded(String uid) {
                updateUI("onPaymentPageLoaded: " + uid);
            }

            @Override
            public void onPaymentStarted() {
                updateUI("onPaymentStarted");
            }

            @Override
            public void onPaymentCancelled() {
                updateUI("onPaymentCancelled");
            }

            @Override
            public void onPaymentError(String data) {
                updateUI("onPaymentError: " + data);
            }

            @Override
            public void onPaymentCompleted(String data) {

                updateUI("onPaymentCompleted: " + data);

                try {

                    JSONObject jsonObject = new JSONObject(data);

                    ipgClient.getStatus(getApplicationContext(), jsonObject.getString("uid"), jsonObject.getString("resultIndicator"), new IPGStatusListener() {
                        @Override
                        public void onResponse(String data) {
                            updateUI("onResponse: " + data);
                        }
                    });

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void updateUI(String message) {
        TextView textViewResponse = findViewById(R.id.textViewResponse);
        textViewResponse.setText(message + "\n" + textViewResponse.getText().toString());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}