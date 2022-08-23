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
    fun startPayment(activity: FragmentActivity, ipgPayment: IPGPayment, ipgListener: IPGListener, dialog: Boolean = true) {

        this.ipgPayment = ipgPayment
        this.ipgListener = ipgListener

        if (dialog) {
            IPGFragment(this).show(activity.supportFragmentManager, IPGFragment::class.java.simpleName)
        } else {
            val intent = Intent(activity, IPGActionView::class.java)
            intent.putExtra(PAYableIPGClient::class.java.simpleName, this)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @JvmOverloads
    fun startPayment(activity: FragmentActivity, uid: String, ipgListener: IPGListener, dialog: Boolean = true) {

        this.uid = uid
        this.ipgListener = ipgListener

        if (dialog) {
            IPGFragment(this).show(activity.supportFragmentManager, IPGFragment::class.java.simpleName)
        } else {
            val intent = Intent(activity, IPGActionView::class.java)
            intent.putExtra(PAYableIPGClient::class.java.simpleName, this)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    internal fun generatePaymentURL(ipgPayment: IPGPayment? = null, uid: String? = null): String? {

        if (uid != null) {

            return "${getServerUrl()}/${environment.name.lowercase()}/?uid=$uid"
        }

        if (ipgPayment != null) {

            return "${getServerUrl()}/${environment.name.lowercase()}/" +

                    "?merchantKey=$merchantKey" +
                    "&merchantToken=$merchantToken" +
                    "&integrationType=MSDK" +
                    "&integrationVersion=1.0.1" +
                    "&integrationEnv=$environment" +

                    "&refererUrl=$refererUrl" +
                    "&logoUrl=$logoUrl" +

                    (if (ipgPayment.notificationUrl != null) "&notificationUrl=${ipgPayment.notificationUrl}" else "") +
                    "&returnUrl=${getServerUrl()}/${environment.name.lowercase()}/status-view" +

                    "&buttonType=${ipgPayment.uiConfig.buttonType}" +
                    "&statusViewDuration=${ipgPayment.uiConfig.statusViewDuration}" +

                    "&amount=${ipgPayment.amount}" +
                    "&currencyCode=${ipgPayment.currencyCode}" +
                    "&orderDescription=${Uri.encode(ipgPayment.orderDescription)}" +

                    "&customerFirstName=${ipgPayment.customerFirstName}" +
                    "&customerLastName=${ipgPayment.customerLastName}" +
                    "&customerEmail=${ipgPayment.customerEmail}" +
                    "&customerMobilePhone=${ipgPayment.customerMobilePhone}" +

                    "&billingAddressStreet=${ipgPayment.billingAddressCity}" +
                    "&billingAddressCity=${ipgPayment.billingAddressCity}" +
                    "&billingAddressCountry=${ipgPayment.billingAddressCountry}" +
                    "&billingAddressPostcodeZip=${ipgPayment.billingAddressPostcodeZip}" +
                    "&billingAddressStateProvince=${ipgPayment.billingAddressStateProvince}" +

                    "&shippingContactFirstName=${ipgPayment.shippingContactFirstName}" +
                    "&shippingContactLastName=${ipgPayment.shippingContactLastName}" +
                    "&shippingContactEmail=${ipgPayment.shippingContactEmail}" +
                    "&shippingContactMobilePhone=${ipgPayment.shippingContactMobilePhone}" +
                    "&shippingAddressStreet=${ipgPayment.shippingAddressStreet}" +
                    "&shippingAddressCity=${ipgPayment.shippingAddressCity}" +
                    "&shippingAddressCountry=${ipgPayment.shippingAddressCountry}" +
                    "&shippingAddressPostcodeZip=${ipgPayment.shippingAddressPostcodeZip}" +
                    "&shippingAddressStateProvince=${ipgPayment.shippingAddressStateProvince}"
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