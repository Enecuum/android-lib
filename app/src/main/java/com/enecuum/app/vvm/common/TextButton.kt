package com.enecuum.app.vvm.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.enecuum.app.R
import kotlinx.android.synthetic.main.layout_text_button.view.*

class TextButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.layout_text_button, this)
    }

    fun configure(
        @StringRes textRes: Int,
        @DrawableRes iconRes: Int? = null,
        backStyle: BackStyle = BackStyle.BLUE,
        allCaps: Boolean = false
    ) {
        buttonText.setText(textRes)
        buttonText.isAllCaps = allCaps
        iconRes?.let {
            buttonImage.setImageResource(it)
            buttonImage.isVisible = true
        }
        when (backStyle) {
            BackStyle.BLUE -> setBackgroundResource(R.drawable.background_button_blue)
            BackStyle.VIOLET -> setBackgroundResource(R.drawable.background_button_violet)
            BackStyle.GRAY -> setBackgroundResource(R.drawable.background_button_gray)
            BackStyle.GRAY_DISABLED -> {
                setBackgroundResource(R.drawable.background_button_gray_disabled)
                buttonText.setTextColor(ContextCompat.getColor(context, R.color.secondary_color))
            }
        }
    }

    fun setText(@StringRes textRes: Int) {
        buttonText.setText(textRes)
    }

    fun setText(text: String) {
        buttonText.text = text
    }

    fun setEnabled() {
        setBackgroundResource(R.drawable.background_button_blue)
        buttonText.setTextColor(ContextCompat.getColor(context, R.color.colorTextWhite))
    }

    fun setDisabled() {
        setBackgroundResource(R.drawable.background_button_gray_disabled)
        buttonText.setTextColor(ContextCompat.getColor(context, R.color.secondary_color))
        this.setOnClickListener {}
    }

    enum class BackStyle {
        BLUE, VIOLET, GRAY, GRAY_DISABLED
    }
}