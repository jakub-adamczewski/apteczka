package com.example.apteczka.auth.register

import com.example.apteczka.auth.util.isValidEmail
import java.lang.Exception

object RegisterFragmentContract {
    data class State(
        val email: String,
        val password: String,
        val name: String,
        val loading: Boolean,
    ) {
        val registerEnabled: Boolean
            get() = email.isValidEmail() && password.trim().length >= 8 && name.trim().length >= 5
    }

    sealed class Effect {
        object Registered : Effect()
        data class RegisterError(val exception: Exception?) : Effect()
    }

    sealed class Intent {
        data class EmailTextChanged(val text: String) : Intent()
        data class PasswordTextChanged(val text: String) : Intent()
        data class NameTextChanged(val text: String) : Intent()
        object RegisterCLicked : Intent()
    }

    val INITIAL_STATE = State(
        email = "",
        password = "",
        name = "",
        loading = false
    )

}