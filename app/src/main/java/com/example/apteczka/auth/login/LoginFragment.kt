package com.example.apteczka.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.apteczka.R
import com.example.apteczka.auth.login.LoginFragmentContract.Effect
import com.example.apteczka.auth.login.LoginFragmentContract.Intent
import com.example.apteczka.auth.util.setTextIfChanged
import com.example.apteczka.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        collectState()
        collectEffects()
    }

    private fun setUpViews() = binding.run {
        emailEditText.addTextChangedListener {
            viewModel.setIntent(Intent.EmailTextChanged(it.toString()))
        }
        passwordEditText.addTextChangedListener {
            viewModel.setIntent(Intent.PasswordTextChanged(it.toString()))
        }
        loginButton.run {
            setOnClickListener {
                viewModel.setIntent(Intent.LoginClicked)
            }
        }
    }

    private fun collectState() = lifecycleScope.launch {
        viewModel.state.collect { state ->
            binding.run {
                emailEditText.setTextIfChanged(state.email)
                passwordEditText.setTextIfChanged(state.password)
                progress.isVisible = state.loading
                loginButton.run {
                    isEnabled = state.loginEnabled
                }
            }
        }
    }

    private fun collectEffects() = lifecycleScope.launch {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.LogInError -> {
                    Snackbar.make(requireView(),effect.exception?.message ?: getText(R.string.wrong_creds), Snackbar.LENGTH_LONG).show()
                }
                Effect.LoggedIn -> {
                    Snackbar.make(requireView(),"Zalogowano.", Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(R.id.to_content)
                    activity?.finish()
                }
            }
        }
    }

}