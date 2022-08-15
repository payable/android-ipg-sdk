package com.payable.ipg

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.payable.ipg.model.IPGListener
import com.payable.ipg.model.IPGPayment
import com.payable.ipg.ui.IPGActionView
import com.payable.ipg.ui.IPGFragment
import java.io.Serializable

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
    var ipgListener: IPGListener? = null

    private fun getServerUrl(): String {
        return if (environment == Environment.PRODUCTION) {
            "https://us-central1-payable-mobile.cloudfunctions.net/ipg"
        } else {
            "https://5f3e-112-134-211-41.in.ngrok.io/payable-mobile/us-central1/ipg"
        }
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

    internal fun generatePaymentURL(): String? {
        return this.ipgPayment?.let { generatePaymentURL(it) }
    }

    private fun generatePaymentURL(ipgPayment: IPGPayment): String {

        return "${getServerUrl()}/" +

                "?merchantKey=$merchantKey" +
                "&merchantToken=$merchantToken" +
                "&integrationType=MSDK" +
                "&integrationVersion=1.0.1" +
                "&integrationEnv=$environment" +

                "&refererUrl=$refererUrl" +
                "&logoUrl=$logoUrl" +

                (if (ipgPayment.notificationUrl != null) "&notificationUrl=${ipgPayment.notificationUrl}" else "") +
                "&returnUrl=${getServerUrl()}/status-view" +

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
}