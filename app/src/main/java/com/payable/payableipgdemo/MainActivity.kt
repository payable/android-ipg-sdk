package com.payable.payableipgdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.payable.ipg.model.IPGListener
import com.payable.ipg.model.IPGPayment
import com.payable.ipg.model.IPGUIConfig
import com.payable.ipg.PAYableIPGClient
import com.payable.payableipgdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val payableIPGClient = PAYableIPGClient(
            "A748BFC24F8F6C61",
            "A8907A75E36A210DE82CDB65430B2E1F",
            "https://www.payable.lk",
            "https://bizenglish.adaderana.lk/wp-content/uploads/NOLIMIT-logo.jpg",
            PAYableIPGClient.Environment.PRODUCTION,
            true
        )

        binding.btnStart.setOnClickListener {

            val ipgPayment = IPGPayment(
                binding.editAmount.text.toString().toDouble(),
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
                IPGUIConfig(2, 120)
            )

            payableIPGClient.startPayment(this, ipgPayment, object : IPGListener {

                override fun onPaymentPageLoaded() {
                    updateUI("onPaymentPageLoaded")
                    Toast.makeText(applicationContext, "onPaymentPageLoaded", Toast.LENGTH_LONG).show()
                }

                override fun onPaymentStarted() {
                    updateUI("onPaymentStarted")
                    Toast.makeText(applicationContext, "onPaymentStarted", Toast.LENGTH_LONG).show()
                }

                override fun onPaymentCancelled() {
                    updateUI("onPaymentCancelled")
                    Toast.makeText(applicationContext, "onPaymentCancelled", Toast.LENGTH_LONG).show()
                }

                override fun onPaymentError(data: String) {
                    updateUI("onPaymentError: $data")
                    Toast.makeText(applicationContext, "onPaymentError: $data", Toast.LENGTH_LONG).show()
                }

                override fun onPaymentCompleted(data: String) {
                    updateUI("onPaymentCompleted: $data")
                    Toast.makeText(applicationContext, "onPaymentCompleted: $data", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun updateUI(message: String) {
        binding.textViewResponse.text = "$message\n${binding.textViewResponse.text}"
    }
}
