package com.example.bubblespopup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import kotlinx.android.synthetic.main.activity_main.view.*
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
    private var radius = 5.0f
    private var animation: ViewPropertyAnimator? = null


    private fun animateThis() {
        if (animation != null) {
            animation!!.cancel()
        }
        animation = animate()
        animation!!.x(this.x + speedX).y(this.y + speedY).setDuration(animationDuration).withEndAction{animateThis()}.start()


    }
    private fun startingSpeed() {
        val randomX = (-speedLimit..speedLimit).shuffled().first()
        val randomY = (-speedLimit..speedLimit).shuffled().first()
        speedX = randomX.toFloat()
        speedY = randomY.toFloat()
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
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

    private fun changeColor(): Color {
        val count = Random.nextInt(0,4)
        var color = Color.RED
        for (i in 0..count){
            color = color.nextCl()
        }
        return color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
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

    private fun CheckCollision(v1: View, v2: View): Boolean {
        val R1 = Rect(v1.left, v1.top, v1.right, v1.bottom)
        val R2 = Rect(v2.left, v2.top, v2.right, v2.bottom)
        return R1.intersect(R2)
    }

}