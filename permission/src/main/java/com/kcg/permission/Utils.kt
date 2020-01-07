package com.kcg.permission

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

object Utils {

    fun reSizingView(activity: AppCompatActivity, view: View, widthRatio: Float, heightRatio: Float) {
        WebViewFragment.with(activity).setWidthRatio(widthRatio)
        WebViewFragment.with(activity).setHeightRatio(heightRatio)

        val display = activity.resources.displayMetrics

        val params = ConstraintLayout.LayoutParams((display.widthPixels * widthRatio).toInt(), (display.heightPixels * heightRatio).toInt())
                .apply {
                    leftToLeft = R.id.content
                    rightToRight = R.id.content
                    bottomToBottom = R.id.content
                    topToTop = R.id.content

                    leftMargin = 0
                    rightMargin = 0
                    bottomMargin = 0
                    topMargin = 0
                }

        view.layoutParams = params
    }

}