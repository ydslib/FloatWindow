package com.ydslib.floatwindow.ktx

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler

class FloatLifecycle(
    applicationContext: Context,
    private var showFlag: Boolean, var lifecycleListener: LifecycleListener?
) : BroadcastReceiver(), ActivityLifecycleCallbacks {
    private val SYSTEM_DIALOG_REASON_KEY = "reason"
    private val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
    private val delay: Long = 300
    private var mHandler: Handler? = null
    private var startCount = 0
    private var resumeCount = 0
    private var appBackground = false

    private var num = 0


    companion object {
        private var sResumedListener: ResumedListener? = null
        fun setResumedListener(resumedListener: ResumedListener?) {
            sResumedListener = resumedListener
        }
    }

    init {
        num++
        mHandler = Handler()
        (applicationContext.applicationContext as Application).registerActivityLifecycleCallbacks(
            this
        )
        applicationContext.registerReceiver(this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
    }


    private fun needShow(activity: Activity): Boolean {
//        if (activities == null) {
//            return true
//        }
//        for (a in activities!!) {
//            if (a.isInstance(activity)) {
//                return showFlag
//            }
//        }
        return !showFlag
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        startCount++
    }


    override fun onActivityResumed(activity: Activity) {
        if (sResumedListener != null) {
            num--
            if (num == 0) {
                sResumedListener!!.onResumed()
                sResumedListener = null
            }
        }
        resumeCount++
        lifecycleListener!!.onShow()
//        if (needShow(activity)) {
//            mLifecycleListener!!.onShow()
//        } else {
//            mLifecycleListener!!.onHide()
//        }
//        if (appBackground) {
//            appBackground = false
//        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumeCount--
        mHandler!!.postDelayed({
            if (resumeCount == 0) {
                appBackground = true
                lifecycleListener!!.onBackToDesktop()
            }
        }, delay)
    }

    override fun onActivityStopped(activity: Activity) {
        startCount--
        if (startCount == 0) {
            lifecycleListener!!.onBackToDesktop()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }


    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action != null && action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
            if (SYSTEM_DIALOG_REASON_HOME_KEY == reason) {
                lifecycleListener!!.onBackToDesktop()
            }
        }
    }


}