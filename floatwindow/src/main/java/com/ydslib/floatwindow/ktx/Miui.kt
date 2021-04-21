package com.ydslib.floatwindow.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.ydslib.floatwindow.*
import java.util.*

class Miui {



    companion object{
        private val miui = "ro.miui.ui.version.name"
        private val miui5 = "V5"
        private val miui6 = "V6"
        private val miui7 = "V7"
        private val miui8 = "V8"
        private val miui9 = "V9"
        private var mPermissionListenerList = arrayListOf<PermissionListener>()
        private var mPermissionListener: PermissionListener? = null
        fun rom(): Boolean {
            return Build.MANUFACTURER == "Xiaomi"
        }
        /**
         * Android6.0以下申请权限
         */
        fun req(context: Context, permissionListener: PermissionListener) {
            if (PermissionUtil.hasPermission(context)) {
                permissionListener.onSuccess()
                return
            }


            mPermissionListener = object : PermissionListener{
                override fun onSuccess() {
                    for (listener in mPermissionListenerList) {
                        listener.onSuccess()
                    }
                    mPermissionListenerList.clear()
                }

                override fun onFail() {
                    for (listener in mPermissionListenerList) {
                        listener.onFail()
                    }
                    mPermissionListenerList.clear()
                }

            }
            req_(context)
            mPermissionListenerList.add(permissionListener)
        }


        private fun req_(context: Context) {
            when (getProp()) {
                miui5 -> reqForMiui5(context)
                miui6, miui7 -> reqForMiui67(context)
                miui8, miui9 -> reqForMiui89(context)
            }

            FloatLifecycle.setResumedListener(object : ResumedListener {
                override fun onResumed() {
                    if (PermissionUtil.hasPermission(context)) {
                        mPermissionListener!!.onSuccess()
                    } else {
                        mPermissionListener!!.onFail()
                    }
                }
            })
        }
        private fun getProp(): String? {
            return Rom.getProp(miui)
        }
        private fun reqForMiui5(context: Context) {
            val packageName = context.packageName
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Rom.isIntentAvailable(intent, context)) {
                context.startActivity(intent)
            } else {
//            LogUtil.e("intent is not available!")
            }
        }

        private fun reqForMiui67(context: Context) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Rom.isIntentAvailable(intent, context)) {
                context.startActivity(intent)
            } else {
//            LogUtil.e("intent is not available!")
            }
        }

        private fun reqForMiui89(context: Context) {
            var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Rom.isIntentAvailable(intent, context)) {
                context.startActivity(intent)
            } else {
                intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.setPackage("com.miui.securitycenter")
                intent.putExtra("extra_pkgname", context.packageName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (Rom.isIntentAvailable(intent, context)) {
                    context.startActivity(intent)
                } else {
//                LogUtil.e("intent is not available!")
                }
            }
        }


        /**
         * 有些机型在添加TYPE-TOAST类型时会自动改为TYPE_SYSTEM_ALERT，通过此方法可以屏蔽修改
         * 但是...即使成功显示出悬浮窗，移动的话也会崩溃
         */
        private fun addViewToWindow(wm: WindowManager, view: View, params: WindowManager.LayoutParams) {
            setMiUI_International(true)
            wm.addView(view, params)
            setMiUI_International(false)
        }
        private fun setMiUI_International(flag: Boolean) {
            try {
                val BuildForMi = Class.forName("miui.os.Build")
                val isInternational = BuildForMi.getDeclaredField("IS_INTERNATIONAL_BUILD")
                isInternational.isAccessible = true
                isInternational.setBoolean(null, flag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }












}