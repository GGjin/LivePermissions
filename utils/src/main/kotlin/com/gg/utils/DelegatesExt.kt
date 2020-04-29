package com.gg.utils

import android.content.Context

/**
 *  Creator : GG
 *  Time    : 2017/11/17
 *  Mail    : gg.jin.yu@gmail.com
 *  Explain :
 */
object DelegatesExt {

    fun <T : Any> preference(context: Context, name: String, default: T)
            = Preference(context, name, default)

}