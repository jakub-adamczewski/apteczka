package com.example.apteczka.auth.preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.apteczka.R
import com.example.apteczka.auth.api.AuthManager
import com.example.apteczka.databinding.FragmentLoginBinding
import com.example.apteczka.databinding.FragmentLoginPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginPreviewFragment : Fragment() {

    private val binding: FragmentLoginPreviewBinding by lazy { FragmentLoginPreviewBinding.inflate(layoutInflater)}

    @Inject lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(authManager.isAnyUserSignedIn) {
            findNavController().navigate(R.id.to_content)
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            loginButton.setOnClickListener {
                findNavController().navigate(R.id.to_login)
            }
            registerButton.setOnClickListener {
                findNavController().navigate(R.id.to_register)
            }
        }
    }

}