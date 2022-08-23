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

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivityJava extends AppCompatActivity {

    private ActivityMainJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_java);
    }

    private void updateUI(String message) {
        binding.textViewResponse.setText(message + "\n" + binding.textViewResponse.getText().toString());
    }
}