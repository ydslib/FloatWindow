package com.ydslib.floatwindow.ktx

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

class Util {

    companion object{
        fun inflate(applicationContext: Context, layoutId: Int): View {
            val inflate =
                applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return inflate.inflate(layoutId, null)
        }

        private var sPoint: Point? = null

        fun getScreenWidth(context: Context): Int {
            if (sPoint == null) {
                sPoint = Point()
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getSize(sPoint)
            }
            return sPoint!!.x
        }

        fun getScreenHeight(context: Context): Int {
            if (sPoint == null) {
                sPoint = Point()
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getSize(sPoint)
            }
            return sPoint!!.y
        }

        fun isViewVisible(view: View): Boolean {
            return view.getGlobalVisibleRect(Rect())
        }
    }


}