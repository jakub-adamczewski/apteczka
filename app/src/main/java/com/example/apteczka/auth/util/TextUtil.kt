package com.example.apteczka.auth.util

import android.widget.EditText
import android.widget.TextView

fun EditText.setTextIfChanged(newText: String) {
    if (text.toString() != newText) {
        setText(newText)
    }
}