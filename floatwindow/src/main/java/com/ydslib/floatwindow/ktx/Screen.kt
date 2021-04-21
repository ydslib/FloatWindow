package com.ydslib.floatwindow.ktx

import androidx.annotation.IntDef


@Retention(AnnotationRetention.SOURCE)
@IntDef(Screen.WIDTH,Screen.HEIGHT)
public annotation class Screen(){
    companion object{
        const val WIDTH = 0
        const val HEIGHT = 1
    }
}
