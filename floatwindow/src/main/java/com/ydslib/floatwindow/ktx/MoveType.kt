package com.ydslib.floatwindow.ktx

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(MoveType.FIXED,MoveType.INACTIVE,MoveType.ACTIVE,MoveType.SLIDE,MoveType.BACK)
annotation class MoveType(){
    companion object{
        const val FIXED = 0
        const val INACTIVE =1
        const val ACTIVE = 2
        const val SLIDE = 3
        const val BACK = 4
    }
}
