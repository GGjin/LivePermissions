package com.gg.livepermissions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData

/**
 *  Create by GG on 2020/4/28
 *  mail is gg.jin.yu@gmail.com
 */

const val TAG = "live_permissions"
val tag = Any()
@Volatile
private var liveFragment: LiveFragment? = null

fun FragmentActivity.applyPermissions(vararg permissions: String): MutableLiveData<PermissionResult> {
    return getLiveFragment(this.supportFragmentManager).requestPermission(permissions)
}

fun Fragment.applyPermissions(vararg permissions: String): MutableLiveData<PermissionResult> {
    return getLiveFragment(this.childFragmentManager).requestPermission(permissions)
}

private fun getLiveFragment(fragmentManager: FragmentManager) =
    liveFragment ?: synchronized(tag) {
        liveFragment ?: if (fragmentManager.findFragmentByTag(TAG) == null) LiveFragment().run {
            fragmentManager.beginTransaction().add(this, TAG).commitNow()
            this
        } else fragmentManager.findFragmentByTag(TAG) as LiveFragment
    }

