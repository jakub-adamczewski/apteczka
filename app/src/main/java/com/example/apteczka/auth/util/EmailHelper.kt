package com.example.apteczka.auth


fun String.isEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

