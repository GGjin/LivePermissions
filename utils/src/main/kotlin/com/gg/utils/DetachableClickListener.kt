package com.gg.utils

import android.view.ViewTreeObserver.OnWindowAttachListener
import android.app.Dialog
import android.content.DialogInterface



/**
 * Creator : GG
 * Date    : 2018/4/25
 * Mail    : gg.jin.yu@gmai.com
 * Explain :
 */
class DetachableClickListener private constructor(private var delegateOrNull: DialogInterface.OnClickListener?) : DialogInterface.OnClickListener {

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (delegateOrNull != null) {
            delegateOrNull!!.onClick(dialog, which)
        }
    }

    fun clearOnDetach(dialog: Dialog) {
        dialog.window
                ?.decorView
                ?.viewTreeObserver
                ?.addOnWindowAttachListener(object : OnWindowAttachListener {
                    override fun onWindowAttached() {}
                    override fun onWindowDetached() {
                        delegateOrNull = null
                    }
                })
    }

    companion object {

        fun wrap(delegate: DialogInterface.OnClickListener): DetachableClickListener {
            return DetachableClickListener(delegate)
        }
    }
}