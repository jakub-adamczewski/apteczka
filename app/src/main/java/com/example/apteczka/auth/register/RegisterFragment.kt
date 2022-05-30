package com.example.apteczka.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.apteczka.R
import com.example.apteczka.auth.util.setTextIfChanged
import com.example.apteczka.auth.register.RegisterFragmentContract.Intent
import com.example.apteczka.auth.register.RegisterFragmentContract.Effect
import com.example.apteczka.auth.register.RegisterFragmentContract.State
import com.example.apteczka.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val binding: FragmentRegisterBinding by lazy {
        FragmentRegisterBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: RegisterFragmentViewModel by viewModels()

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
        nameEditText.addTextChangedListener {
            viewModel.setIntent(Intent.NameTextChanged(it.toString()))
        }
    }

    private fun collectState() = lifecycleScope.launch {
        viewModel.state.collect { state ->
            binding.run {
                emailEditText.setTextIfChanged(state.email)
                passwordEditText.setTextIfChanged(state.password)
                nameEditText.setTextIfChanged(state.name)
                progress.isVisible = state.loading
                registerButton.run {
                    isEnabled = state.registerEnabled
                    setOnClickListener {
                        viewModel.setIntent(Intent.RegisterCLicked)
                    }
                }
            }
        }
    }

    private fun collectEffects() = lifecycleScope.launch {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.RegisterError -> {
                    Snackbar.make(requireView(),effect.exception?.message ?: getText(R.string.wrong_creds), Snackbar.LENGTH_LONG).show()
                }
                Effect.Registered -> {
                    Snackbar.make(requireView(),"Zarejestrowano, zaloguj się.", Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(R.id.to_login)
                }
            }
        }
    }

}