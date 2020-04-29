package com.gg.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Creator : GG
 * Date    : 2018/1/11
 * Mail    : gg.jin.yu@gmai.com
 * Explain :
 */
class NetManager(private var applicationContext: Context) {
    private var status: Boolean? = false

    val isConnectedToInternet: Boolean?
        get() {
            val conManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = conManager.activeNetworkInfo
            return ni != null && ni.isConnected
        }
}