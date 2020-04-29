package com.gg.livepermissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.gg.utils.clickWithTrigger
import com.gg.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tag = "permission"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = LogConfiguration.Builder()
            .logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE) // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
            .tag("XLog") // 指定 TAG，默认为 "X-LOG"
            .st(2)
            .b().build()

        XLog.init(config)
        button2.clickWithTrigger {
            applyPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).observe(this, Observer {
                XLog.w("-------1")
                XLog.w(it.toString())
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
