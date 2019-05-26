package com.silvericekey.skutilslibrary.NetUtils

interface NetCallback<T> {
    fun onSuccess(response: T?)
}