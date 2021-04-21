package com.ydslib.demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ydslib.floatwindow.ktx.*

class MainActivity : AppCompatActivity() {
    lateinit var floatWindow: FloatWindow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view: View = LayoutInflater.from(this).inflate(R.layout.float_layout, null)
        val imageView:ImageView = view.findViewById(R.id.imageView)
        floatWindow = FloatWindow(this, view).apply {
            mView = view
            setWidth(Screen.WIDTH, 0.2f)
            setHeight(Screen.WIDTH, 0.2f)
            setX(Screen.WIDTH, 0.8f)
            setY(Screen.HEIGHT, 0.7f)
            setMoveType(MoveType.ACTIVE, 100, 0)
            setMoveStyle(500, BounceInterpolator())
            mViewStateListener = mStateListener
            mPermissionListener = mPerListener
            mDragView = imageView
            show()
        }


        imageView.setOnClickListener {
            Toast.makeText(this,"点击事件",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        floatWindow.dismiss()
    }

    override fun onStart() {
        super.onStart()
        floatWindow.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatWindow.clear()
    }

    private val mPerListener: PermissionListener = object : PermissionListener {
        override fun onSuccess() {
            Log.d("MainActivity:", "onSuccess")
        }

        override fun onFail() {
            Log.d("MainActivity:", "onFail")
        }
    }

    private val mStateListener: ViewStateListener = object : ViewStateListener {
        override fun onPositionUpdate(x: Int, y: Int) {
            Log.d("MainActivity:", "onPositionUpdate: x=$x y=$y")
        }

        override fun onShow() {
            Log.d("MainActivity:", "onShow")
        }

        override fun onHide() {
            Log.d("MainActivity:", "onHide")
        }

        override fun onDismiss() {
            Log.d("MainActivity:", "onDismiss")
        }

        override fun onMoveAnimStart() {
            Log.d("MainActivity:", "onMoveAnimStart")
        }

        override fun onMoveAnimEnd() {
            Log.d("MainActivity:", "onMoveAnimEnd")
        }

        override fun onBackToDesktop() {
            Log.d("MainActivity:", "onBackToDesktop")
        }
    }
}