package com.example.bubblespopup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import kotlin.math.abs
import kotlin.random.Random

const val ViewHeight = 200
const val ViewWidth = 200
const val animationDuration: Long = 10
const val speedLimit = 15
const val ClosingGap = 15
const val PushBackCoefficient = 1.5f
const val WallHitPushBack = 10

@SuppressLint("ViewConstructor")
class Bubble constructor(
    context: Context,
    xAxis: Float,
    yAxis: Float
) : View(context) {
    private val xCoordinate = xAxis
    private val yCoordinate = yAxis
    var speedX = 0f
    var speedY = 0f
    private var displayHeight = 0
    private var displayWidth = 0
    private var radius = 0f
    private var animation: ViewPropertyAnimator? = null
    private lateinit var thisLayout: ViewGroup
    private fun animateThis() {
        cancelAnimationIfNecessary()
        animation = animate()
        isHorizontalHit()
        isVerticalHit()
        animation?.x(this.x + speedX)?.y(this.y + speedY)?.setUpdateListener {
            thisLayout = this.parent as ViewGroup
            getCollisions(thisLayout.childCount)
        }?.setDuration(animationDuration)?.withEndAction {
            if (!deleteIfDead())
                animateThis()
        }?.start()
    }

    private fun getCollisions(count: Int) {
        (1..count).forEach { i ->
            if (thisLayout.getChildAt(i) != null) {
                val v = thisLayout.getChildAt(i)
                if (v != this) {
                    if (isOverlap(v)) {
                        slowDown()
                        sendBackALittle()
                        speedX = -speedX
                        speedY = -speedY
                    }
                }
            }
        }
    }

    /*get random speed*/
    private fun startingSpeed() {
        val randomX = (-speedLimit..speedLimit).shuffled().first()
        val randomY = (-speedLimit..speedLimit).shuffled().first()
        speedX = randomX.toFloat()
        speedY = randomY.toFloat()
    }

    private fun cancelAnimationIfNecessary() {
        animation?.cancel()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private fun isHorizontalHit() {
        if (this.x <= 0) {
            slowDown()
            this.x += WallHitPushBack
            speedX = -(speedX)
        } else
            if (this.x >= displayWidth - width) {
                this.x -= WallHitPushBack
                speedX = -(speedX)
                slowDown()
            }


    }

    private fun isVerticalHit() {
        if (this.y <= 0) {
            this.y += WallHitPushBack
            speedY = -(speedY)
            slowDown()
        } else
            if (this.y >= displayHeight - height) {
                this.y -= WallHitPushBack
                speedY = -(speedY)
                slowDown()
            }


    }

    private fun isOverlap(bubble: View): Boolean {
        val location = IntArray(2)

        getLocationInWindow(location)
        val rect1 = Rect(
            location[0] + ClosingGap,
            location[1] + ClosingGap,
            location[0] + width - ClosingGap,
            location[1] + height - ClosingGap
        )

        bubble.getLocationInWindow(location)
        val rect2 = Rect(
            location[0] + ClosingGap,
            location[1] + ClosingGap,
            location[0] + bubble.width - ClosingGap,
            location[1] + bubble.height - ClosingGap
        )
        return rect1.intersect(rect2)
    }

    private fun sendBackALittle() {
        this.x -= speedX * PushBackCoefficient
        this.y -= speedY * PushBackCoefficient
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = ViewWidth
        val desiredHeight = ViewHeight
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = width.toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        cancelAnimationIfNecessary()
        speedX = 0f
        speedY = 0f
        thisLayout.removeView(this)
        return true
    }

    init {
        isClickable = true
    }


    /*get screen dimensions*/
    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.x = xCoordinate
        this.y = yCoordinate
        displayWidth = getScreenWidth()
        displayHeight = getScreenHeight()
        /* draw a random color circle and have a black stroke*/
        paint.style = Paint.Style.FILL
        paint.color = randomColor()
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius / 2, paint)
        paint.color = Color.BLACK.rgb
        paint.style = Paint.Style.STROKE
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius / 2, paint)
        getHitBox(canvas,false)  /*use (true) if hitbox needed*/
        /* go to animation*/
        startingSpeed()
        animateThis()
    }
    fun getHitBox(canvas: Canvas,flag:Boolean){
        if (flag){
            canvas.drawRect(
                0f + ClosingGap,
                0f + ClosingGap,
                width - ClosingGap.toFloat(),
                height - ClosingGap.toFloat(),
                paint
            )
        }
    }
    private fun randomColor(): Int {
        val i = Random.nextInt(1, Color.values().size)
        return Color.values()[i].rgb
    }

    private fun slowDown() {
        speedX *= 0.9f
        speedY *= 0.9f
    }

    private fun deleteIfDead(): Boolean {
        return if (abs(speedX) < 0.05f && abs(speedY) < 0.05f) {
            speedX = 0f
            speedY = 0f
            cancelAnimationIfNecessary()
            thisLayout.removeView(this)

            true
        } else
            false
    }
}