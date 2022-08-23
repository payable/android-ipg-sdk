package com.payable.ipg.model

import java.io.Serializable

data class IPGPayment @JvmOverloads constructor(

    val amount: Double,
    val currencyCode: String,
    val orderDescription: String,

    val customerFirstName: String,
    val customerLastName: String,
    val customerEmail: String,
    val customerMobilePhone: String,

    val billingAddressStreet: String,
    val billingAddressCity: String,
    val billingAddressCountry: String,
    val billingAddressPostcodeZip: String,
    
    val billingAddressStateProvince: String? = null,

    val shippingContactFirstName: String? = null,
    val shippingContactLastName: String? = null,
    val shippingContactEmail: String? = null,
    val shippingContactMobilePhone: String? = null,

    val shippingAddressStreet: String? = null,
    val shippingAddressCity: String? = null,
    val shippingAddressCountry: String? = null,
    val shippingAddressPostcodeZip: String? = null,
    val shippingAddressStateProvince: String? = null,

    val notificationUrl: String? = null,
    val uiConfig: IPGUIConfig = IPGUIConfig()

) : Serializable