package com.meditrust.findadoctor.core.util

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class NoUnderlineClickableSpan : ClickableSpan() {
    override fun onClick(widget: View) {
        TODO("Not yet implemented")
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
        ds.color = Color.parseColor("#005096") // Change to your desired color
    }
}