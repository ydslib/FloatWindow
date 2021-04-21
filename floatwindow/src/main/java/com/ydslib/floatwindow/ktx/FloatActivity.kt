package com.ydslib.floatwindow.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class FloatActivity:AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAlertWindowPermission()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 756232212)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 756232212) {
            if (PermissionUtil.hasPermissionOnActivityResult(this)) {
                mPermissionListener?.onSuccess()
            } else {
                mPermissionListener?.onFail()
            }
        }
        finish()
    }

    companion object{
        private var mPermissionListener: PermissionListener? = null
        private val mPermissionListenerList = arrayListOf<PermissionListener>()
        @Synchronized
        fun request(context: Context, permissionListener: PermissionListener) {
            if (PermissionUtil.hasPermission(context)) {
                permissionListener.onSuccess()
                return
            }
            mPermissionListener = object : PermissionListener {
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
            val intent = Intent(context, FloatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            mPermissionListenerList.add(permissionListener)
        }
    }


}