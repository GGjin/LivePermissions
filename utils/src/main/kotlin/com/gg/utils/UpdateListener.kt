package com.gg.utils

/**
 *  Creator : GG
 *  Time    : 2017/12/5
 *  Mail    : gg.jin.yu@gmail.com
 *  Explain :
 */
interface UpdateListener {

    fun onSucceed(url: String)

    fun onFail(e: Throwable?)
}