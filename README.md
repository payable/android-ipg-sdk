### PAYable IPG SDK - Android Integration

![](https://i.imgur.com/ERpCDa7.png)

PAYable IPG SDK - [android-ipg-sdk.payable.lk](https://android-ipg-sdk.payable.lk) | [Create Issue](https://github.com/payable/android-ipg-sdk/issues/new)

[![Build Status](https://travis-ci.com/payable/android-ipg-sdk.svg?branch=master)](https://travis-ci.com/payable/android-ipg-sdk)
[![](https://jitpack.io/v/payable/android-ipg-sdk.svg)](https://jitpack.io/#payable/android-ipg-sdk)

<hr>

### Initialization

1. Add the below repository into your project level build.gradle file.

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the below dependency into your module level `build.gradle` file.

```gradle
implementation 'com.github.payable:android-ipg-sdk:1.0.4'
```

<hr>

### Implementation

<b>1.</b> Import PAYable IPG SDK package.

```dart
import 'package:payable_ipg/payable_ipg.dart';
```

<b>2.</b> Create PAYable IPG client with `PAYableIPGClient`.

```java 
PAYableIPGClient ipgClient = new PAYableIPGClient(
        "YOUR_MERCHANT_KEY",
        "YOUR_MERCHANT_TOKEN",
        "YOUR_REQUEST_URL",
        "YOUR_COMPANY_LOGO",
        PAYableIPGClient.Environment.SANDBOX, // optional
        true // optional
);
```

<b>3.</b> Prepare `IPGPayment` to initiate the payment.

```java
IPGPayment ipgPayment = new IPGPayment(
    amount,
    currency,
    orderDescription,
    customerFirstName,
    customerLastName,
    customerEmail,
    customerMobilePhone,
    billingAddressStreet,
    billingAddressCity,
    billingAddressCountry,
    billingAddressPostcodeZip
);
```

<b>4.</b> Call `startPayment` method from `ipgClient` to perform the payment.

```java
ipgClient.startPayment(activity, ipgPayment, new IPGListener() {

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
    }
});
```

<hr/>

### Example Usage

```java
public class MainActivityJava extends AppCompatActivity {

    private ActivityMainJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_java);

        PAYableIPGClient ipgClient = new PAYableIPGClient(
                "KEY",
                "TOKEN",
                "URL",
                "LOGO",
                PAYableIPGClient.Environment.SANDBOX,
                true
        );

        IPGPayment ipgPayment = new IPGPayment(
                10.00,
                "LKR",
                "Netflix",
                "Aslam",
                "Kasun",
                "user@example.lk",
                "0771234567",
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
        binding.textViewResponse.setText(message + "\n" + binding.textViewResponse.getText().toString());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
```

### Advanced Usage

### API Documentation

This document contains all the HTTP APIs used in this package.

[ipg-mobile-api.payable.lk](https://ipg-mobile-api.payable.lk)

### Demo

![](https://raw.githubusercontent.com/payable/android-ipg-sdk/master/screen.gif)

PAYable IPG SDK - Android Integration