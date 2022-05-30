package com.example.apteczka.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apteczka.auth.api.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import com.example.apteczka.auth.login.LoginFragmentContract.State
import com.example.apteczka.auth.login.LoginFragmentContract.Effect
import com.example.apteczka.auth.login.LoginFragmentContract.INITIAL_STATE
import com.example.apteczka.auth.login.LoginFragmentContract.Intent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state: Flow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            authManager.effect
                .collect {authEffect ->
                    when(authEffect) {
                        AuthManager.AuthEffect.SignedIn -> {
                            _effect.send(Effect.LoggedIn)
                        }
                        is AuthManager.AuthEffect.SignInError -> {
                            _effect.send(Effect.LogInError(authEffect.exception))
                        }
                        is AuthManager.AuthEffect.SignUpError -> Unit
                        AuthManager.AuthEffect.SignedUp -> Unit
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
            Intent.LoginClicked -> {
                _state.value.let {
                    authManager.signIn(
                        email = it.email,
                        password = it.password
                    )
                }
            }

        }
    }
}