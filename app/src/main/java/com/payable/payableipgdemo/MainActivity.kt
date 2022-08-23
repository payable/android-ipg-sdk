package com.payable.payableipgdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.payable.ipg.PAYableIPGClient
import com.payable.ipg.model.*
import com.payable.payableipgdemo.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var payableIPGClient: PAYableIPGClient? = null;
    private var uid: String? = null
    private var resultIndicator: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnSandbox.setOnClickListener {
            startPayment(PAYableIPGClient.Environment.SANDBOX)
        }

        binding.btnProduction.setOnClickListener {
            startPayment(PAYableIPGClient.Environment.PRODUCTION)
        }

        binding.btnSessionId.setOnClickListener {
            if (payableIPGClient != null && uid != null) {
                startPayment(ipgEnvironment = payableIPGClient!!.environment, uid = uid!!)
            }
        }

        binding.btnStatus.setOnClickListener {
            if (payableIPGClient != null && uid != null && resultIndicator != null) {
                payableIPGClient!!.getStatus(this, uid = uid!!, resultIndicator = resultIndicator!!) {
                    updateUI("onStatus")
                }
            }
        }
    }

    private fun startPayment(ipgEnvironment: PAYableIPGClient.Environment, uid: String? = null) {

        payableIPGClient = if (ipgEnvironment == PAYableIPGClient.Environment.PRODUCTION) {
            PAYableIPGClient(
                "2C60AC94E06CFC1B",
                "DC9420BC7EBC2E38A19A931BB8B099EB",
                "https://ipgmobileteam.payable.lk",
                "https://i.imgur.com/l21F5us.png",
                PAYableIPGClient.Environment.PRODUCTION,
                true
            )
        } else {
            PAYableIPGClient(
                "A75BCD8EF30E529A",
                "B8727C74D29E210F9A297B65690C0140",
                "https://www.sandboxmerdev.payable.lk",
                "https://i.imgur.com/l21F5us.png",
                PAYableIPGClient.Environment.SANDBOX,
                true
            )
        }

        val ipgListener = object : IPGListener {

            override fun onPaymentPageLoaded(uid: String) {
                updateUI("onPaymentPageLoaded")
                this@MainActivity.uid = uid
            }

            override fun onPaymentStarted() {
                updateUI("onPaymentStarted")
            }

            override fun onPaymentCancelled() {
                updateUI("onPaymentCancelled")
            }

            override fun onPaymentError(data: String) {
                updateUI("onPaymentError: $data")
            }

            override fun onPaymentCompleted(data: String) {
                updateUI("onPaymentCompleted: $data")
                this@MainActivity.resultIndicator = JSONObject(data).getString("resultIndicator")
            }
        }

        if (uid == null) {

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
                "Western", // optional
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
                IPGUIConfig(2, 10)
            )

            payableIPGClient!!.startPayment(this, ipgPayment, ipgListener)

        } else {

            payableIPGClient!!.startPayment(this, uid, ipgListener)
        }
    }

    private fun updateUI(message: String) {
        binding.textViewResponse.text = "$message\n${binding.textViewResponse.text}"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
