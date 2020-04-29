package com.gg.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.mzw.utils.R
import java.util.regex.Pattern


/**
 *  Creator : GG
 *  Time    : 2017/10/16
 *  Mail    : gg.jin.yu@gmail.com
 *  Explain : The `fragment` is added to the container view with id `frameId`. The operation is
 *            performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

var gson = Gson()

fun Any.toJson(): String = gson.toJson(this)

inline fun <reified T> String.toObject(): T = gson.fromJson<T>(this)

inline fun <reified T> Gson.fromJson(json: String) = fromJson(json, T::class.java)

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

fun Activity.setTransparent() {
    if (Build.VERSION.SDK_INT >= 21) {
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
    }
}

fun FragmentTransaction.addAnimation() {
    setCustomAnimations(
        R.anim.slide_right_in,
        R.anim.slide_left_out,
        R.anim.slide_left_in,
        R.anim.slide_right_out
    )
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

/**
 * 获取屏幕宽度
 */
fun Context.screenWidth(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

/**
 * 获取屏幕高度
 */
fun Context.screenHeight(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.heightPixels
}

/**
 * 获取当前app的状态
 */
fun Context.isApkInDebug(): Boolean {
    return try {
        val info = this.applicationInfo
        info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    } catch (e: Exception) {
        false
    }
}

/**
 * 获取当前线程名字
 */
fun Context.curProcessName(): String? {
    val pid = android.os.Process.myPid()
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (appProcess in activityManager.runningAppProcesses) {
        if (appProcess.pid == pid) {
            return appProcess.processName
        }
    }
    return null
}

/**
 * dp转px
 */
fun Context.dip2px(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(), this.resources.displayMetrics
    ).toInt()
}

/**
 * px转dp
 */
fun Context.px2dip(px: Int): Int {
    val scale = this.resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

/**
 * 设置添加屏幕的背景透明度
 */
fun Activity.backgroundAlpha(alpha: Float) {
    val lp = this.window.attributes
    lp.alpha = alpha
    this.window.attributes = lp
}

/**
 * 获取版本号
 *
 * @return 当前应用的版本号
 */
fun Context.versionName(): String =
    try {
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, 0)
        info.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        this.getString(R.string.can_not_find_version_name)
    }

/**
 * 获取版本号
 *
 * @return 当前应用的版本号
 */
fun Context.versionCode(): Int =
    try {
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, 0)
        info.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }


/**
 * 通过反射的方式获取状态栏高度
 *
 * @return
 */
fun Context.statusBarHeight(): Int {
    try {
        val c = Class.forName("com.android.internal.R\$dimen")
        val obj = c.newInstance()
        val field = c.getField("status_bar_height")
        val x = Integer.parseInt(field.get(obj).toString())
        return this.resources.getDimensionPixelSize(x)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return 0
}

/**
 * 获取底部导航栏高度
 *
 * @return
 */
fun Context.getNavigationBarHeight(): Int {
    val resources = this.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    //获取NavigationBar的高度
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * 获取是否存在NavigationBar
 */
fun Context.checkDeviceHasNavigationBar(): Boolean {
    var hasNavigationBar = false
    val rs = this.resources
    val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id)
    }
    try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val m = systemPropertiesClass.getMethod("get", String::class.java)
        val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
        if ("1" == navBarOverride) {
            hasNavigationBar = false
        } else if ("0" == navBarOverride) {
            hasNavigationBar = true
        }
    } catch (e: Exception) {

    }

    return hasNavigationBar

}

/**
 * 以兼容的方式获取颜色值
 */
fun Context.getCompatColor(@ColorRes id: Int): Int =
    ContextCompat.getColor(this, id)

/**
 * 以兼容的方式获取drawable
 */
fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? =
    ContextCompat.getDrawable(this, id)


fun Context.toast(str: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.applicationContext, str, length).show()
}

fun Context.toast(int: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.applicationContext, int, length).show()
}

/**
 *
 * 通过包名判断程序是否安装
 *
 */
fun Context.checkAppInstalled(name: String): Boolean {

    if (TextUtils.isEmpty(name)) {
        return false
    }

    val packageInfo: PackageInfo? = try {
        packageManager.getPackageInfo(
            name, 0
        )
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    return packageInfo != null
}

fun AppCompatActivity.context(): Context = this

fun Fragment.toast(str: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context?.applicationContext, str, length).show()
}

fun Fragment.toast(int: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context?.applicationContext, int, length).show()
}


fun String.isPhoneNum(): Boolean {
    val p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$")
    val m = p.matcher(this)
    return m.matches()
}

fun Int?.isNullOrZero(): Boolean {
    return this == null || this == 0
}

fun Double?.isNullOrZero(): Boolean {
    return this == null || this == 0.0
}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L

}

//fun View.showSnackbar(snackbarText: String, timeLength: Int) {
//    Snackbar.make(this, snackbarText, timeLength).show()
//}

/**
 * 去除字符串中的空格、回车、换行符、制表符等
 * @param str
 * @return
 */
fun String.replaceSpecialStr(): String {
    val p = Pattern.compile("\\s*|\t|\r|\n")
    val m = p.matcher(this)
    return m.replaceAll("")
}
