package com.gg.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*


/**
 *  Creator : GG
 *  Time    : 2017/10/18
 *  Mail    : gg.jin.yu@gmail.com
 *  Explain :
 */
class AppManager private constructor() {

    companion object {
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = AppManager()
    }

    private var mActivities: Stack<Activity> = Stack()


    /**
     * 添加Activity到堆栈
     */
    fun attach(activity: Activity) {
        mActivities.add(activity)
    }

    /**
     * 将指定Activity移除
     */
    fun detach(activity: Activity) {
        mActivities.remove(activity)

//        var size = -1
//        for (i in mActivities.indices) {
//            val a = mActivities[i]
//            if (a == activity) {
//                size = i
//                break
//            }
//        }
//        if (size != -1)
//            mActivities.removeAt(size)
    }

    /**
     * Get current activity (the last into Stack)
     */
    fun currentActivity(): Activity {
        return mActivities.lastElement()
    }

    /**
     * Finish current activity (the last into Stack)
     */
    fun finishActivity() {
        val a = mActivities.lastElement()
        a.finish()
    }

    /**
     * Finish the input activity
     */
    fun finishActivity(activity: Activity?) {
        var size = -1
        for (i in mActivities.indices) {
            val a = mActivities[i]
            if (a == activity) {
                size = i
                break
            }
        }
        if (size != -1) {
            mActivities.removeAt(size)
            activity?.finish()
        }
    }

    /**
     * finish the activity of afferent class
     */
    fun finishActivity(cls: Class<*>) {
        for (i in mActivities.indices) {
            val a = mActivities[i]
            if (a.javaClass.canonicalName == cls.canonicalName) {
                mActivities.removeAt(i)
                a.finish()
                break
            }
        }
    }

    /**
     * Judge the input Activity is live or die
     */
    fun isLive(activity: Activity): Boolean {
        return mActivities.contains(activity)
    }

    /**
     * Judge the Activity instance of the input class is live or die
     * (the Activity can have more than one object)
     */
    fun isLive(cls: Class<*>): Boolean {
        mActivities.forEach {
            if (it.javaClass.canonicalName == cls.canonicalName)
                return true
        }
        return false
    }

    fun getActivity(cls: Class<*>): Activity? {
        mActivities.forEach {
            if (it.javaClass.canonicalName == cls.canonicalName)
                return it
        }
        return null
    }

    /**
     * Finish all activity of th mActivities and make mActivities clear
     */
    fun finishAllActivity() {
        mActivities.forEach {
            it?.finish()
        }
        mActivities.clear()
    }

    /**
     * app exit
     */
    @SuppressLint("MissingPermission")
    fun appExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(context.packageName)
            System.exit(0)
        } catch (e: Exception) {
        }

    }

}