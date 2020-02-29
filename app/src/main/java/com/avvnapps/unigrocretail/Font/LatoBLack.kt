package com.avvnapps.unigrocretail.Font

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class LatoBLack : TextView {

    constructor(context: Context) : super(context) {
        applyCustomFont(context)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context)

    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {

        val customFont = FontCacheRagular.getTypeface("Bold.otf", context)

        typeface = customFont
    }
}

