package com.ydslib.floatwindow.ktx

import android.view.View

interface IFloatWindow {
    fun show()

    fun hide()

    fun isShowing(): Boolean

    fun getX(): Int

    fun getY(): Int

    fun updateX(x: Int)

    fun updateX(@Screen screenType: Int, ratio: Float)

    fun updateY(y: Int)

    fun updateY(@Screen screenType: Int, ratio: Float)

    fun getView(): View?

    fun dismiss()
}