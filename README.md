# LivePermissions
使用kotlin拓展属性一行代码申请权限
## 使用方法
### 1.添加依赖
在 build.gradle中的repositories最后面添加
```
maven { url 'https://jitpack.io' }
```

在主项目的build.gradle中添加
```
 implementation 'com.github.GGJin:LivePermissions:1.0.1'
 ```
### 2.使用方法
在Activity或者Fragment中的任何位置直接调用
```
 applyPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).observe(this, Observer {
              
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
```
