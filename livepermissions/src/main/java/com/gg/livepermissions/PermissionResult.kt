package com.gg.livepermissions


/**
 *  Create by GG on 2020/4/28
 *  mail is gg.jin.yu@gmail.com
 */
sealed class PermissionResult(val permissions: Array<String>? = null) {
    object Grant : PermissionResult()
    class Deny(permission: Array<String>? = null) : PermissionResult(permission)
    class Rationale(permission: Array<String>? = null) : PermissionResult(permission)

    override fun toString(): String {
        return permissions?.asList().toString()
    }
}