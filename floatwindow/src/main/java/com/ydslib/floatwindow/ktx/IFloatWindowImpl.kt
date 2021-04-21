package com.ydslib.floatwindow.ktx

import android.animation.*
import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs

class IFloatWindowImpl(var floatWindow: FloatWindow, var mView: View?) : IFloatWindow {

    private var mFloatView: FloatView

    private var isShow = false
    private var once = true
    private var mAnimator: ValueAnimator? = null
    private var mDecelerateInterpolator: TimeInterpolator? = null
    private var downX = 0f
    private var downY = 0f
    private var upX = 0f
    private var upY = 0f
    private var mClick = false
    private var mSlop = 0
    private var mFloatLifecycle: FloatLifecycle? = null

    init {
        if (floatWindow.mMoveType == MoveType.FIXED) {
            mFloatView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                FloatPhone(floatWindow.mContext, floatWindow.mPermissionListener)
            } else {
                FloatToast(floatWindow.mContext)
            }
        } else {
            mFloatView =
                FloatPhone(floatWindow.mContext, floatWindow.mPermissionListener)
            initTouchEvent()
        }
        mFloatView.run {
            setSize(floatWindow.mWidth, floatWindow.mHeight)
            setGravity(floatWindow.gravity, floatWindow.xOffset, floatWindow.yOffset)
            setView(floatWindow.mView)
        }

        mFloatLifecycle = FloatLifecycle(
            floatWindow.mContext,
            floatWindow.mShow,
            object : LifecycleListener {
                override fun onShow() {
                    show()
                }

                override fun onHide() {
                    hide()
                }

                override fun onBackToDesktop() {
                    if (!floatWindow.mDesktopShow) {
                        hide()
                    }
                    floatWindow.mViewStateListener?.onBackToDesktop()
                }
            })
    }


    private fun cancelAnimator() {
        mAnimator?.cancel()
    }

    private fun startAnimator() {
        if (floatWindow.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = DecelerateInterpolator()
            }
            floatWindow.mInterpolator = mDecelerateInterpolator
        }
        mAnimator?.run {
            interpolator = floatWindow.mInterpolator
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mAnimator?.apply {
                        removeAllUpdateListeners()
                        removeAllListeners()
                        mAnimator = null
                    }
                    floatWindow.mViewStateListener?.onMoveAnimEnd()
                }
            })
            setDuration(floatWindow.mDuration)?.start()
        }
        floatWindow.mViewStateListener?.onMoveAnimStart()
    }

    private fun initTouchEvent() {

        when (floatWindow.mMoveType) {
            MoveType.INACTIVE -> {
            }
            else -> if (mView == null) getView() else mView!!.setOnTouchListener(object :
                OnTouchListener {
                var lastX = 0f
                var lastY = 0f
                var changeX = 0f
                var changeY = 0f
                var newX = 0
                var newY = 0

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            downX = event.rawX
                            downY = event.rawY
                            lastX = event.rawX
                            lastY = event.rawY
                            cancelAnimator()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            changeX = event.rawX - lastX
                            changeY = event.rawY - lastY
                            newX = (mFloatView.getX() + changeX).toInt()
                            newY = (mFloatView.getY() + changeY).toInt()
                            mFloatView.updateXY(newX, newY)
                            if (floatWindow.mViewStateListener != null) {
                                floatWindow.mViewStateListener?.onPositionUpdate(newX, newY)
                            }
                            lastX = event.rawX
                            lastY = event.rawY
                        }
                        MotionEvent.ACTION_UP -> {
                            upX = event.rawX
                            upY = event.rawY
                            mClick = abs(upX - downX) > mSlop + 5 || abs(upY - downY) > mSlop + 5
                            when (floatWindow.mMoveType) {
                                MoveType.SLIDE -> {
                                    val startX = mFloatView.getX()
                                    val endX: Int =
                                        if (startX * 2 + v.width > Util.getScreenWidth(floatWindow.mContext))
                                            Util.getScreenWidth(floatWindow.mContext) - v.width - floatWindow.mSlideRightMargin
                                        else floatWindow.mSlideLeftMargin

                                    mAnimator = ObjectAnimator.ofInt(startX, endX)
                                    mAnimator?.addUpdateListener { animation ->
                                        val x = animation.animatedValue as Int
                                        mFloatView.updateX(x)
                                        if (floatWindow.mViewStateListener != null) {
                                            floatWindow.mViewStateListener?.onPositionUpdate(
                                                x,
                                                upY.toInt()
                                            )
                                        }
                                    }
                                    startAnimator()
                                }
                                MoveType.BACK -> {
                                    val pvhX = PropertyValuesHolder.ofInt(
                                        "x",
                                        mFloatView.getX(),
                                        floatWindow.xOffset
                                    )
                                    val pvhY = PropertyValuesHolder.ofInt(
                                        "y",
                                        mFloatView.getY(),
                                        floatWindow.yOffset
                                    )
                                    mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY)
                                    mAnimator?.addUpdateListener { animation ->
                                        val x = animation.getAnimatedValue("x") as Int
                                        val y = animation.getAnimatedValue("y") as Int
                                        mFloatView.updateXY(x, y)
                                        if (floatWindow.mViewStateListener != null) {
                                            floatWindow.mViewStateListener?.onPositionUpdate(x, y)
                                        }
                                    }
                                    startAnimator()
                                }
                                else -> {
                                }
                            }
                        }
                        else -> {
                        }
                    }
                    return mClick
                }
            })
        }
    }


    override fun show() {
        if (once) {
            mFloatView.initView()
            once = false
            isShow = true
        } else {
            if (isShow) {
                return
            }
            getView()!!.visibility = View.VISIBLE
            isShow = true
        }
        floatWindow.mViewStateListener?.onShow()
    }

    override fun hide() {
        if (once || !isShow) {
            return
        }
        getView()!!.visibility = View.INVISIBLE
        isShow = false
        floatWindow.mViewStateListener?.onHide()
    }

    override fun isShowing(): Boolean {
        return isShow
    }

    override fun getX(): Int {
        return mFloatView.getX()
    }

    override fun getY(): Int {
        return mFloatView.getY()
    }

    override fun updateX(x: Int) {
        checkMoveType()
        floatWindow.xOffset = x
        mFloatView.updateX(x)
    }

    private fun checkMoveType() {
        require(floatWindow.mMoveType != MoveType.FIXED) { "FloatWindow of this tag is not allowed to move!" }
    }

    override fun updateX(screenType: Int, ratio: Float) {
        checkMoveType()
        floatWindow.xOffset =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(floatWindow.mContext) else Util.getScreenHeight(
                floatWindow.mContext
            )) * ratio).toInt()
        mFloatView.updateX(floatWindow.xOffset)
    }

    override fun updateY(y: Int) {
        checkMoveType()
        floatWindow.yOffset = y
        mFloatView.updateY(y)
    }

    override fun updateY(screenType: Int, ratio: Float) {
        checkMoveType()
        floatWindow.yOffset =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(floatWindow.mContext) else Util.getScreenHeight(
                floatWindow.mContext
            )) * ratio).toInt()
        mFloatView.updateY(floatWindow.yOffset)
    }

    override fun getView(): View? {
        mSlop = ViewConfiguration.get(floatWindow.mContext).scaledTouchSlop
        return floatWindow.mView
    }

    override fun dismiss() {
        mFloatView.dismiss()
        isShow = false
        floatWindow.mViewStateListener?.onDismiss()
    }
}