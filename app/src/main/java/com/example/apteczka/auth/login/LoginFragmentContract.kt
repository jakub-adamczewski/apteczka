package com.example.apteczka.auth.login

import com.example.apteczka.auth.util.isValidEmail
import java.lang.Exception

object LoginFragmentContract {
    data class State(
        val email: String,
        val password: String,
        val loading: Boolean,
    ) {
        val loginEnabled: Boolean
            get() = email.isValidEmail() && password.length >= 8
    }

    sealed class Effect {
        object LoggedIn : Effect()
        data class LogInError(val exception: Exception?) : Effect()
    }

    sealed class Intent {
        data class EmailTextChanged(val text: String) : Intent()
        data class PasswordTextChanged(val text: String) : Intent()
        object LoginClicked : Intent()
    }

    val INITIAL_STATE = State(
        email = "",
        password = "",
        loading = false
    )

}