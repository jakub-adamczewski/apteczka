package com.example.apteczka.auth.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor() {

    private val _effect = Channel<AuthEffect>()
    val effect: Flow<AuthEffect> = _effect.receiveAsFlow()

    private val auth: FirebaseAuth
        get() = Firebase.auth

    val isAnyUserSignedIn: Boolean
        get() = auth.currentUser != null

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun signUp(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    )
                    runBlocking {
                        _effect.send(AuthEffect.SignedUp)
                    }
                } else {
                    runBlocking {
                        _effect.send(AuthEffect.SignUpError(task.exception))
                    }
                }
            }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    runBlocking {
                        _effect.send(AuthEffect.SignedIn)
                    }
                } else {
                    runBlocking {
                        _effect.send(AuthEffect.SignInError(task.exception))
                    }
                }
            }
    }

    sealed class AuthEffect {
        object SignedIn : AuthEffect()
        data class SignInError(val exception: Exception?) : AuthEffect()
        object SignedUp : AuthEffect()
        data class SignUpError(val exception: Exception?) : AuthEffect()
    }

}