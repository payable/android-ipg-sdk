package com.payable.ipg

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.payable.ipg.model.IPGListener
import com.payable.ipg.model.IPGPayment
import com.payable.ipg.model.IPGStatusListener
import com.payable.ipg.ui.IPGActionView
import com.payable.ipg.ui.IPGFragment
import java.io.Serializable
import java.util.*
import com.retrofit.lite.services.APITask
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


class PAYableIPGClient @JvmOverloads constructor(
    private val merchantKey: String,
    private val merchantToken: String,
    private val refererUrl: String,
    private val logoUrl: String,
    val environment: Environment,
    val debug: Boolean = false
) : Serializable {

    enum class Environment {
        SANDBOX, PRODUCTION
    }

    val requestCode = 3569
    var ipgPayment: IPGPayment? = null
    var uid: String? = null
    var ipgListener: IPGListener? = null

    private fun getServerUrl(): String {
        return "https://us-central1-payable-mobile.cloudfunctions.net/ipg"
    }

    @JvmOverloads
    fun startPayment(activity: FragmentActivity, ipgPayment: IPGPayment, ipgListener: IPGListener) {

        this.ipgPayment = ipgPayment
        this.ipgListener = ipgListener

        IPGFragment(this).show(activity.supportFragmentManager, IPGFragment::class.java.simpleName)
    }

    @JvmOverloads
    fun startPayment(activity: FragmentActivity, uid: String, ipgListener: IPGListener) {

        this.uid = uid
        this.ipgListener = ipgListener

        IPGFragment(this).show(activity.supportFragmentManager, IPGFragment::class.java.simpleName)
    }

    internal fun generatePaymentURL(ipgPayment: IPGPayment? = null, uid: String? = null): String? {

        if (uid != null) {

            return "${getServerUrl()}/${environment.name.lowercase()}/?uid=$uid"
        }

        if (ipgPayment != null) {

            val queryList = arrayListOf<String>()

            queryList.add("${getServerUrl()}/${environment.name.lowercase()}/")

            queryList.add("?merchantKey=$merchantKey")
            queryList.add("&merchantToken=$merchantToken")
            queryList.add("&integrationType=MSDK")
            queryList.add("&integrationVersion=1.0.1")
            queryList.add("&integrationEnv=$environment")

            queryList.add("&refererUrl=$refererUrl")
            queryList.add("&logoUrl=$logoUrl")
            queryList.add("&returnUrl=${getServerUrl()}/${environment.name.lowercase()}/status-view")

            queryList.add("&amount=${ipgPayment.amount}")
            queryList.add("&currencyCode=${ipgPayment.currencyCode}")
            queryList.add("&orderDescription=${Uri.encode(ipgPayment.orderDescription)}")

            queryList.add("&customerFirstName=${ipgPayment.customerFirstName}")
            queryList.add("&customerLastName=${ipgPayment.customerLastName}")
            queryList.add("&customerEmail=${ipgPayment.customerEmail}")
            queryList.add("&customerMobilePhone=${ipgPayment.customerMobilePhone}")
            queryList.add("&billingAddressStreet=${ipgPayment.billingAddressCity}")
            queryList.add("&billingAddressCity=${ipgPayment.billingAddressCity}")
            queryList.add("&billingAddressCountry=${ipgPayment.billingAddressCountry}")
            queryList.add("&billingAddressPostcodeZip=${ipgPayment.billingAddressPostcodeZip}")

            queryList.add("&buttonType=${ipgPayment.uiConfig.buttonType}")
            queryList.add("&statusViewDuration=${ipgPayment.uiConfig.statusViewDuration}")

            if (ipgPayment.billingAddressStateProvince != null)
                queryList.add("&billingAddressStateProvince=${ipgPayment.billingAddressStateProvince}")

            if (ipgPayment.shippingContactFirstName != null)
                queryList.add("&shippingContactFirstName=${ipgPayment.shippingContactFirstName}")

            if (ipgPayment.shippingContactLastName != null)
                queryList.add("&shippingContactLastName=${ipgPayment.shippingContactLastName}")

            if (ipgPayment.shippingContactEmail != null)
                queryList.add("&shippingContactEmail=${ipgPayment.shippingContactEmail}")

            if (ipgPayment.shippingContactMobilePhone != null)
                queryList.add("&shippingContactMobilePhone=${ipgPayment.shippingContactMobilePhone}")

            if (ipgPayment.shippingAddressStreet != null)
                queryList.add("&shippingAddressStreet=${ipgPayment.shippingAddressStreet}")

            if (ipgPayment.shippingAddressCity != null)
                queryList.add("&shippingAddressCity=${ipgPayment.shippingAddressCity}")

            if (ipgPayment.shippingAddressCountry != null)
                queryList.add("&shippingAddressCountry=${ipgPayment.shippingAddressCountry}")

            if (ipgPayment.shippingAddressPostcodeZip != null)
                queryList.add("&shippingAddressPostcodeZip=${ipgPayment.shippingAddressPostcodeZip}")

            if (ipgPayment.shippingAddressStateProvince != null)
                queryList.add("&shippingAddressStateProvince=${ipgPayment.shippingAddressStateProvince}")

            if (ipgPayment.notificationUrl != null)
                queryList.add("&notificationUrl=${ipgPayment.notificationUrl}")

            return queryList.joinToString("")
        }

        return null
    }

    fun getStatus(context: Context, uid: String, resultIndicator: String, onResponse: ((data: String) -> Unit)) {
        getStatus(context, uid, resultIndicator, object : IPGStatusListener {
            override fun onResponse(data: String) {
                onResponse(data)
            }
        })
    }

    fun getStatus(context: Context, uid: String, resultIndicator: String, listener: IPGStatusListener) {

        val serverUrl = "${getServerUrl()}/${environment.name.lowercase()}/status-view?uid=$uid&resultIndicator=$resultIndicator&responseType=json"

        APITask.from(context).sendGET(200, serverUrl, null, object : APITask.Listener {

            override fun onSuccess(pid: Int, status: Int, headers: Map<String, String>, body: String) {
                listener.onResponse(body)
            }

            override fun onFailed(pid: Int, ex: Exception) {
                listener.onResponse(ex.message ?: ex.toString())
            }
        })

    }
}