package com.anwesh.uiprojects.tricreaterotbouncyview

/**
 * Created by anweshmishra on 09/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 6
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val deg : Float = 60f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30