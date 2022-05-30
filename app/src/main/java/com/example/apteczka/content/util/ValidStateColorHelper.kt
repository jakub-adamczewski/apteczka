package com.example.apteczka.content.util

import androidx.annotation.ColorRes
import com.example.apteczka.R
import com.example.apteczka.data.ValidityState

@ColorRes
fun ValidityState.getColor(): Int =
    when (this) {
        ValidityState.VALID -> R.color.validity_green
        ValidityState.CLOSE_TO_INVALID -> R.color.validity_orange
        ValidityState.INVALID -> R.color.validity_red
    }