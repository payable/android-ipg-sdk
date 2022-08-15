package com.payable.ipg.model

interface IPGListener {

    fun onPaymentPageLoaded()
    fun onPaymentStarted()
    fun onPaymentCancelled()
    fun onPaymentError(data: String)
    fun onPaymentCompleted(data: String)
}