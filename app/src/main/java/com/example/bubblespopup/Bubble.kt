package com.example.bubblespopup

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import kotlin.random.Random


private enum class Color(val rgb: Int) {
    RED(-65536),
    GREEN(-16711936),
    BLUE(-16776961),
    BLACK(-16777216),
    PURPLE(-65281);

    fun nextCl() = when (this) {
        RED -> GREEN
        GREEN -> BLUE
        BLUE -> PURPLE
        else -> RED

    }
}

class Bubble @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var speedX = 0f
    private var speedY = 0f
    private var displayHeight = 0
    private var displayWidth = 0
    private var radius = 5.0f
    private var animation: ViewPropertyAnimator? = null
    private lateinit var thisLayout:ViewGroup


    private fun animateThis() {
        if (animation != null) {
            animation!!.cancel()
        }
        animation = animate()
        /*checking if view hitting the borders*/
        horizontalHit()
        verticalHit()
        animation!!.x(this.x + speedX).y(this.y + speedY).setUpdateListener {
            thisLayout = this.parent as ViewGroup
            var count = thisLayout.childCount
            (0..count).forEach { i ->
                val v: View
                if (thisLayout.getChildAt(i) != null) {
                    v = thisLayout.getChildAt(i)
                    if (v != this) {
                        if (isOverlap(v)) {
                            speedX = -speedX
                            speedY = -speedY
                        }
                    }
                }
            }


        }.setDuration(animationDuration).withEndAction{animateThis()}.start()


    }
    /*get random speed*/
    private fun startingSpeed() {
        val randomX = (-speedLimit..speedLimit).shuffled().first()
        val randomY = (-speedLimit..speedLimit).shuffled().first()
        speedX = randomX.toFloat()
        speedY = randomY.toFloat()
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private fun horizontalHit(){
        if (this.x <= 0) {
            this.x += 40
            speedX = -(speedX)
        } else
            if (this.x >= displayWidth - width){
                this.x -= 40
                speedX = -(speedX)
            }


    }
    private fun verticalHit(){
        if (this.y <= 0) {
            this.y += 40
            speedY = -(speedY)
        } else
            if (this.y >= displayHeight - height){
                this.y -= 40
                speedY = -(speedY)
            }


    }
    private fun isOverlap(bubble: View) : Boolean{
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
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = ViewWidth
        val desiredHeight = ViewHeight
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = width.toFloat()
    }

    init {
        isClickable = true
    }
    /*function making view change color*/
    private fun changeColor(): Color {
        val count = Random.nextInt(0, 4)
        var color = Color.RED
        for (i in 0..count){
            color = color.nextCl()
        }
        return color
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
        displayWidth = getScreenWidth()
        displayHeight = getScreenHeight()
       /* draw a random color circle and have a black stroke*/
        paint.style = Paint.Style.FILL
        paint.color = changeColor().rgb
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius / 2, paint)
        paint.color = Color.BLACK.rgb
        paint.style = Paint.Style.STROKE
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius / 2, paint)
   /*     go to animation*/
        startingSpeed()
        animateThis()
    }


}