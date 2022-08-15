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
    val billingAddressStateProvince: String,

    val shippingContactFirstName: String,
    val shippingContactLastName: String,
    val shippingContactEmail: String,
    val shippingContactMobilePhone: String,

    val shippingAddressStreet: String,
    val shippingAddressCity: String,
    val shippingAddressCountry: String,
    val shippingAddressPostcodeZip: String,
    val shippingAddressStateProvince: String,

    val notificationUrl: String? = null,
    val uiConfig: IPGUIConfig = IPGUIConfig()

) : Serializable