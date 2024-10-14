package com.meditrust.findadoctor.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView

object TextViewUtils {

    fun setupClickableTextView(
        context: Context,
        textView: TextView,
        fullText: String,
        clickableParts: Map<String, String>
    ) {
        val spannableString = SpannableString(fullText)

        clickableParts.forEach { (clickableText, url) ->
            setClickableSpan(context, spannableString, clickableText, url)
        }

        textView.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setClickableSpan(
        context: Context,
        spannableString: SpannableString,
        clickableText: String,
        url: String
    ) {
        val clickableSpan = object : NoUnderlineClickableSpan() {
            override fun onClick(widget: View) {
                val uri = Uri.parse(url)
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
        val startIndex = spannableString.toString().indexOf(clickableText)
        val endIndex = startIndex + clickableText.length
        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }


}
