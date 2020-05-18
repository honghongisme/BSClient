package com.example.picturerecognize.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

class CustomEditText : EditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    // 小数点后只允许有两位
    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text.toString().contains(".")) {
            if (text!!.length - 1 - text.toString().indexOf(".") > 2) {
                val t = text.toString().subSequence(0,
                    text.toString().indexOf(".") + 3)
                setText(t)
                setSelection(t.length)
            }
        }
        if (text.toString().trim().substring(0) == ".") {
            val t = "0$text"
            setText(t)
            setSelection(2)
        }
        if (text.toString().startsWith("0")
            && text.toString().trim().length > 1) {
            if (text.toString().substring(1, 2) != ".") {
                setText(text!!.subSequence(0, 1))
                setSelection(1)
                return
            }
        }
    }
}