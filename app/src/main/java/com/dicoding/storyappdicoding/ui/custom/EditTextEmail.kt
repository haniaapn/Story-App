package com.dicoding.storyappdicoding.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Patterns
import com.dicoding.storyappdicoding.R
import com.google.android.material.textfield.TextInputLayout

class EditTextEmail: TextInputLayout {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        //

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (editText?.isFocused == true) {
            error =
                if (editText?.text?.isNotEmpty() == true && !Patterns.EMAIL_ADDRESS.matcher(editText?.text.toString())
                        .matches()
                ) {
                    context.getString(R.string.error_valid_email)
                } else {
                    null
                }
        }
    }
}