package com.gg.utils

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModelProvider

/**
 * Creator : GG
 * Date    : 2018/1/12
 * Mail    : gg.jin.yu@gmai.com
 * Explain :
 */
class ViewModelFactory private constructor(
        private val application: Application

) : ViewModelProvider.NewInstanceFactory() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(
                        application
                    )
                            .also { INSTANCE = it }
                }


        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
