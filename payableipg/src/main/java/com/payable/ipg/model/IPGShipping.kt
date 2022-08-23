package com.payable.ipg.model

import java.io.Serializable

data class IPGShipping @JvmOverloads constructor(

    val shippingContactFirstName: String? = null,
    val shippingContactLastName: String? = null,
    val shippingContactEmail: String? = null,
    val shippingContactMobilePhone: String? = null,
    val shippingContactPhone: String? = null,

    val shippingAddressStreet: String? = null,
    val shippingAddressStreet2: String? = null,
    val shippingAddressCity: String? = null,
    val shippingAddressCountry: String? = null,
    val shippingAddressPostcodeZip: String? = null,
    val shippingAddressStateProvince: String? = null

) : Serializable