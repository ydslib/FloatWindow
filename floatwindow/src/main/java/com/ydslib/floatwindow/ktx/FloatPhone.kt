package com.ydslib.floatwindow.ktx

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowManager

class FloatPhone(var context: Context, var permissionListener: PermissionListener?) : FloatView() {

    private var mWindowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var isRemove = false
    private var mView:View?=null
    private var mX:Int = 0
    private  var mY:Int = 0

    private val mLayoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }

    init {
        mLayoutParams.run {
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            windowAnimations = 0
        }
    }

    override fun setSize(width: Int, height: Int) {
        mLayoutParams.run {
            this.width = width
            this.height = height
        }
    }

    override fun setView(view: View?) {
        mView = view
    }

    override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        mLayoutParams.run {
            this.gravity = gravity
            x = xOffset
            y = yOffset
            mX = x
            mY = y
        }

    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            req()
        } else if (Miui.rom()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                req()
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
                Miui.req(context, object : PermissionListener {
                    override fun onSuccess() {
                        mWindowManager.addView(mView, mLayoutParams)
                        permissionListener?.onSuccess()
                    }

                    override fun onFail() {
                        permissionListener?.onFail()
                    }
                })
            }
        } else {
            try {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST
                mWindowManager.addView(mView, mLayoutParams)
            } catch (e: Exception) {
                mWindowManager.removeView(mView)
                req()
            }
        }
    }

    private fun req() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        FloatActivity.request(context, object : PermissionListener {
            override fun onSuccess() {
                mWindowManager.addView(mView, mLayoutParams)
                permissionListener?.onSuccess()
            }

            override fun onFail() {
                permissionListener?.onFail()
            }
        })
    }

    override fun dismiss() {
        isRemove = true
        mWindowManager.removeView(mView)
    }

    override fun updateXY(x: Int, y: Int) {
        if (isRemove) return
        mX = x
        mY = y
        mLayoutParams.x = mX
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun updateX(x: Int) {
        if (isRemove) return
        mX = x
        mLayoutParams.x = mX
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun updateY(y: Int) {
        if (isRemove) return
        mY = y
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun getX(): Int {
        return mX
    }

    override fun getY(): Int {
        return mY
    }
}