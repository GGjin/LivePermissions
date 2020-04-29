package com.gg.livepermissions

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

/**
 *  Create by GG on 2020/4/28
 *  mail is gg.jin.yu@gmail.com
 */
class LiveFragment : Fragment() {

    private val PERMISSIONS_REQUEST_CODE = 0X0077


    lateinit var liveData: MutableLiveData<PermissionResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun requestPermission(permissions: Array<out String>) : MutableLiveData<PermissionResult> {
        liveData = MutableLiveData()
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        return liveData
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val rationalePermissions = ArrayList<String>()
            val denyPermissions = ArrayList<String>()

            grantResults.asList().forEachIndexed { index, value ->
                //权限代码为拒绝状态
                if (value == PackageManager.PERMISSION_DENIED) {
                    //第一次全新进入时，shouldShowRequestPermissionRationale方法将返回false,这里将会执行。
                    //请求权限时如果点了拒绝但是没勾选不再提醒，shouldShowRequestPermissionRationale方法将返回true，这里将不执行。
                    //点了拒绝且勾选了不再提醒，再次进入时，shouldShowRequestPermissionRationale方法也将返回false,并且权限请求将无任何响应，然后可以在下面方法中做些处理，提示用户打开权限。
                    if (shouldShowRequestPermissionRationale(permissions[index])) {
                        rationalePermissions.add(permissions[index])
                    } else {
                        denyPermissions.add(permissions[index])
                    }
                }
            }
            when {
                rationalePermissions.isNotEmpty() -> {
                    liveData.value = PermissionResult.Rationale(rationalePermissions.toTypedArray())
                }
                denyPermissions.isNotEmpty() -> {
                    liveData.value = PermissionResult.Deny(denyPermissions.toTypedArray())
                }
                else -> {
                    liveData.value = PermissionResult.Grant
                }
            }
        }
    }

}