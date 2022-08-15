package com.payable.payableipgdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.payable.ipg.model.IPGListener;
import com.payable.ipg.model.IPGPayment;
import com.payable.ipg.model.IPGUIConfig;
import com.payable.ipg.PAYableIPGClient;
import com.payable.payableipgdemo.databinding.ActivityMainJavaBinding;

public class MainActivityJava extends AppCompatActivity {

    private ActivityMainJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_java);

        PAYableIPGClient ipgClient = new PAYableIPGClient(
                "A748BFC24F8F6C61",
                "A8907A75E36A210DE82CDB65430B2E1F",
                "https://www.payable.lk",
                "https://bizenglish.adaderana.lk/wp-content/uploads/NOLIMIT-logo.jpg",
                PAYableIPGClient.Environment.SANDBOX,
                true
        );

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IPGPayment ipgPayment = new IPGPayment(
                        Double.parseDouble(binding.editAmount.getText().toString()),
                        "LKR",
                        "Netflix",
                        "Aslam",
                        "Anver",
                        "aslam@payable.lk",
                        "0762724081",
                        "Hill Street",
                        "Dehiwala",
                        "LK",
                        "10350",
                        "Western",
                        "Aslam",
                        "Anver",
                        "aslam@payable.lk",
                        "0762724081",
                        "Hill Street",
                        "Dehiwala",
                        "LK",
                        "10350",
                        "Western",
                        "https://us-central1-payable-mobile.cloudfunctions.net/ipg/request-test",
                        new IPGUIConfig(2, 10)
                );

                ipgClient.startPayment(MainActivityJava.this, ipgPayment, new IPGListener() {

                    @Override
                    public void onPaymentPageLoaded() {
                        updateUI("onPaymentPageLoaded");
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
                    }
                });
            }
        });
    }

    private void updateUI(String message) {
        binding.textViewResponse.setText(message + "\n" + binding.textViewResponse.getText().toString());
    }
}