package com.ydslib.floatwindow.ktx

import android.view.View

abstract class FloatView {
    abstract fun setSize(width: Int, height: Int)

    abstract fun setView(view: View?)

    abstract fun setGravity(gravity: Int, xOffset: Int, yOffset: Int)

    abstract fun initView()

    abstract fun dismiss()

    open fun updateXY(x: Int, y: Int) {}

    open fun updateX(x: Int) {}

    open fun updateY(y: Int) {}

    open fun getX(): Int {
        return 0
    }

    open fun getY(): Int {
        return 0
    }
}