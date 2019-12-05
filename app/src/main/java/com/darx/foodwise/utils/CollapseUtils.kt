package com.darx.foodwise

import android.animation.ValueAnimator
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

class CollapseUtils(private val context: Context,
                    private val hide: TextView,
                    private val textView: TextView) {
    fun initDescription(description: String) {
        textView.setOnClickListener {
            val short = textView.measuredHeight
            val h = getHeight(description, textView.textSize, textView.width)
            showSmoothly(it, h).apply {
                doOnStart { textView.maxLines = Int.MAX_VALUE }
            }.start()
            it.setOnClickListener { hideDescription(description, short) }
            hide.setOnClickListener { hideDescription(description, short) }
            showHideBtn()
        }
    }

    private fun hideDescription(
        description: String,
        short: Int
    ) {
        hideHideBtn()
        initDescription(description)
        showSmoothly(textView, short).apply { doOnEnd {
            textView.maxLines = 3 // context.resources.getInteger(R.integer.pet_desc_max_lines)
        } }.start()
    }

    private fun showHideBtn() {
            hide.animate().alpha(1f).setDuration(1000L).setInterpolator(AccelerateDecelerateInterpolator()).start()
    }

    private fun hideHideBtn() {
            hide.animate().alpha(0f).setDuration(450L).setInterpolator(AccelerateDecelerateInterpolator()).start()
    }

    private fun showSmoothly(view: View, targetHeight: Int): ValueAnimator {
        val measuredHeight = view.measuredHeight
        val currentParams = view.layoutParams
        currentParams.height = measuredHeight
        view.layoutParams = currentParams

        val anim = ValueAnimator.ofInt(measuredHeight, targetHeight)
        anim.addUpdateListener { valueAnimator ->
            val layoutParams = view.layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            view.layoutParams = layoutParams
        }
        anim.duration = 450
        return anim
    }

    private fun getHeight(text: String, textSize: Float, deviceWidth: Int): Int {
        val textView = TextView(context)
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val padding = context.resources.getDimensionPixelSize(R.dimen.editable_padding)
        textView.setPadding(padding, padding, padding, padding)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }
}