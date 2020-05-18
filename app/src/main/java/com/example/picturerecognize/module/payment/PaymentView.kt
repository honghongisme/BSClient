package com.example.picturerecognize.module.payment

interface PaymentView {

    fun onGetData(data : MutableList<Any>, expense : Float, income : Float, balance : Float)
}