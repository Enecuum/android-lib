package com.enecuum.app.utils

import android.text.Editable
import android.text.TextWatcher

interface TextChangedWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}