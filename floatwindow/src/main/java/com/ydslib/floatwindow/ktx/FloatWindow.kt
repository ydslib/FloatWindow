package com.ydslib.floatwindow.ktx

import android.animation.TimeInterpolator
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.util.*

class FloatWindow(var mContext: Context, var mView: View) {

    companion object {
        const val defaultTag = "default_float_window_tag"
    }

    var mLayoutId = 0
    var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    var gravity = Gravity.TOP or Gravity.START
    var xOffset = 0
    var yOffset = 0
    var mShow = true

    var mMoveType = MoveType.SLIDE
    var mSlideLeftMargin = 0
    var mSlideRightMargin = 0
    var mDuration: Long = 500
    var mInterpolator: TimeInterpolator? = null
    var mTag = defaultTag
    var mDesktopShow = false
    var mPermissionListener: PermissionListener? = null
    var mViewStateListener: ViewStateListener? = null
    var mFloatWindowMap: HashMap<String, IFloatWindow>? = null
    var mDragView:View?=null

    init {
        if (mFloatWindowMap == null) {
            mFloatWindowMap = HashMap()
        }
        require(!mFloatWindowMap!!.containsKey(mTag)) { "FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow" }
//        require(!mFloatWindowMap!!.containsKey(mTag)) { "FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow" }
//        val floatWindowImpl: IFloatWindow = IFloatWindowImpl(this)
//        mFloatWindowMap?.put(mTag, floatWindowImpl)
    }

    /**
     * 设置window高度
     */
    fun setWidth(@Screen screenType: Int, ratio: Float) {
        mWidth =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(mContext) else Util.getScreenHeight(
                mContext
            )) * ratio).toInt()
    }


    fun setHeight(@Screen screenType: Int, ratio: Float) {
        mHeight =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(mContext) else Util.getScreenHeight(
                mContext
            )) * ratio).toInt()
    }

    fun setX(@Screen screenType: Int, ratio: Float) {
        xOffset =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(mContext) else Util.getScreenHeight(
                mContext
            )) * ratio).toInt()
    }

    fun setY(@Screen screenType: Int, ratio: Float) {
        yOffset =
            ((if (screenType == Screen.WIDTH) Util.getScreenWidth(mContext) else Util.getScreenHeight(
                mContext
            )) * ratio).toInt()
    }


    fun setMoveType(@MoveType moveType: Int) {
        setMoveType(moveType, 0, 0)
    }

    /**
     * 设置带边距的贴边动画，只有 moveType 为 MoveType.slide，设置边距才有意义，这个方法不标准，后面调整
     *
     * @param moveType         贴边动画 MoveType.slide
     * @param slideLeftMargin  贴边动画左边距，默认为 0
     * @param slideRightMargin 贴边动画右边距，默认为 0
     */
    fun setMoveType(
        @MoveType moveType: Int,
        slideLeftMargin: Int,
        slideRightMargin: Int
    ) {
        mMoveType = moveType
        mSlideLeftMargin = slideLeftMargin
        mSlideRightMargin = slideRightMargin
    }

    fun setMoveStyle(duration: Long, interpolator: TimeInterpolator?) {
        mDuration = duration
        mInterpolator = interpolator
    }

    fun setView(view: View) {
        mView = view

    }

    fun setView(@LayoutRes layoutId: Int) {
        mLayoutId = layoutId
        mView = Util.inflate(mContext, mLayoutId)
    }

    fun dismiss() {
        mFloatWindowMap?.run {
            get(defaultTag)?.run {
                if (isShowing()) {
                    dismiss()
                }
            }
        }

    }

    fun show() {
        mFloatWindowMap?.run {
            if (get(mTag) == null) {
                val floatWindowImpl: IFloatWindow = IFloatWindowImpl(this@FloatWindow,mDragView)
                put(mTag, floatWindowImpl)
            }
            get(defaultTag)?.run {
                if (!isShowing()) {
                    show()
                }
            }
        }
    }

    fun clear() {
        mInterpolator = null
        mPermissionListener = null
        mViewStateListener = null
        mFloatWindowMap?.clear()
        mFloatWindowMap = null
    }

    /**
     * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
     *
     * @param show       　过滤类型,子类类型也会生效
     * @param activities 　过滤界面
     */
//        fun setFilter(show: Boolean, vararg activities: Class<*>): B? {
//            mShow = show
//            mActivities = activities
//            return this
//        }
}