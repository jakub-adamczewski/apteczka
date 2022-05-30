package com.example.apteczka.auth.util

import android.util.Patterns

fun CharSequence.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

