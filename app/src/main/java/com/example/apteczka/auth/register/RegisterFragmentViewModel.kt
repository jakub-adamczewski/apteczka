package com.example.apteczka.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apteczka.auth.api.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import com.example.apteczka.auth.register.RegisterFragmentContract.Intent
import com.example.apteczka.auth.register.RegisterFragmentContract.Effect
import com.example.apteczka.auth.register.RegisterFragmentContract.State
import com.example.apteczka.auth.register.RegisterFragmentContract.INITIAL_STATE
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state: Flow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            authManager.effect
                .collect { authEffect ->
                    when (authEffect) {
                        AuthManager.AuthEffect.SignedIn -> Unit
                        is AuthManager.AuthEffect.SignInError -> Unit
                        is AuthManager.AuthEffect.SignUpError -> {
                            _effect.send(Effect.RegisterError(authEffect.exception))
                        }
                        AuthManager.AuthEffect.SignedUp -> {
                            _effect.send(Effect.Registered)
                        }
                    }
                }
        }
    }

    fun setIntent(intent: Intent) = viewModelScope.launch {
        when (intent) {
            is Intent.EmailTextChanged -> {
                _state.emit(
                    _state.value.copy(
                        email = intent.text
                    )
                )
            }
            is Intent.PasswordTextChanged -> {
                _state.emit(
                    _state.value.copy(
                        password = intent.text
                    )
                )
            }
            is Intent.NameTextChanged -> {
                _state.emit(
                    _state.value.copy(
                        name = intent.text
                    )
                )
            }
            Intent.RegisterCLicked -> {
                _state.value.let {
                    authManager.signUp(
                        email = it.email,
                        password = it.password,
                        name = it.name
                    )
                }
            }

        }
    }
}