package com.example.bubblespopup

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var viewsList: ArrayList<View> = ArrayList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        Dynamic View
        */
        backgr.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y
                val newView: Bubble = Bubble(this)
                val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                newView.layoutParams = params
                newView.x = x - ViewWidth / 2
                newView.y = y - ViewHeight / 2
                addContentView(newView, params)
                viewsList.add(newView)
            }
            true
        }
        backgr.setOnLongClickListener{
            Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show()
            true
        }

    }

}

