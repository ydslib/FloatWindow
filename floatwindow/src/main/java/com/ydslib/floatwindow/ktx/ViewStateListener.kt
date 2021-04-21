package com.ydslib.floatwindow.ktx

interface ViewStateListener {
    fun onPositionUpdate(x: Int, y: Int)

    fun onShow()

    fun onHide()

    fun onDismiss()

    fun onMoveAnimStart()

    fun onMoveAnimEnd()

    fun onBackToDesktop()
}