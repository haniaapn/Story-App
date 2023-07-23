package com.dicoding.storyappdicoding.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import com.dicoding.storyappdicoding.R
import com.google.android.material.textfield.TextInputLayout

class EditTextPassword: TextInputLayout {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (editText?.isFocused == true) {
            error = if (editText?.text.toString().length < 8 && editText?.toString()
                    ?.isNotEmpty() == true
            ) {
                context.getString(R.string.error_valid_password)
            } else {
                null
            }
        }
    }

    private fun init() {
        editText?.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }
}