package com.gg.livepermissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gg.utils.toast
import kotlinx.android.synthetic.main.fragment_amin.*

/**
 *  Create by GG on 2020/4/28
 *  mail is gg.jin.yu@gmail.com
 */
class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_amin, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            applyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).observe(this, Observer {
                Log.w("---", "-------2")

                when (it) {
                    is PermissionResult.Grant -> {  //权限允许
                        toast("Grant")
                    }
                    is PermissionResult.Rationale -> {  //权限拒绝
                        it.permissions?.forEach { s ->
                            Log.w(tag, "Rationale:${s}")
                        }
                        toast("Rationale")
                    }
                    is PermissionResult.Deny -> {   //权限拒绝，且勾选了不再询问
                        it.permissions?.forEach { s ->
                            Log.w(tag, "deny:${s}")
                        }
                        toast("deny")
                    }
                }
            })
        }
    }

}