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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Int.ip2() : Int = this % 2
fun Int.sfip2() : Float = 1f - 2 * this.ip2()

fun Canvas.drawRotatingLine(i : Int, scale : Float, w : Float, paint : Paint) {
    val gap : Float = w / (lines * 2)
    save()
    translate(gap * (1 + i / 2 + i.ip2()), 0f)
    rotate(deg * i.sfip2())
    drawLine(0f, 0f, 0f, -gap, paint)
    restore()
}

fun Canvas.drawTriangleAtSteps(scale : Float, w : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawRotatingLine(j, scale, w, paint)
    }
}

fun Canvas.drawTCRBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    save()
    translate(0f, gap * (i + 1))
    drawTriangleAtSteps(scale, w, paint)
    restore()
}

class TriCreateRotBouncyView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TCRBNode(var i : Int, val state : State = State()) {

        private var next : TCRBNode? = null
        private var prev : TCRBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TCRBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTCRBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TCRBNode {
            var curr : TCRBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TCRB(var i : Int) {

        private val root : TCRBNode = TCRBNode(0)
        private var curr : TCRBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriCreateRotBouncyView) {

        private val animator : Animator = Animator(view)
        private val tcrb : TCRB = TCRB(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            tcrb.draw(canvas, paint)
            animator.animate {
                tcrb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tcrb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : TriCreateRotBouncyView {
            val view : TriCreateRotBouncyView = TriCreateRotBouncyView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
