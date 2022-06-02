package com.example.apteczka.content.aidkits

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apteczka.R
import com.example.apteczka.content.aidkits.list.AidKitsAdapter
import com.example.apteczka.content.aidkits.list.KitViewHolder
import com.example.apteczka.databinding.FragmentKitsListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KitsListFragment : Fragment() {

    private val binding by lazy { FragmentKitsListBinding.inflate(layoutInflater) }
    private val viewModel: KitsListFragmentViewModel by viewModels()
    private val kitsAdapter = AidKitsAdapter(
        kitViewHolderFactory = {
            KitViewHolder(
                it
            ) { name ->
                viewModel.setIntent(KitsListFragmentContract.Intent.OnItemClicked(name))
            }
        }
    )

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
        kitsList.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = kitsAdapter
        }
        toolbar.menu.getItem(0).setOnMenuItemClickListener {
            showInputKitNameDialog()
            true
        }
    }

    private fun showInputKitNameDialog() {
        val alert = AlertDialog.Builder(requireContext())
        val editText = EditText(requireContext())
        alert.run {
            setTitle(requireContext().getText(R.string.name_dialog_title))
            setView(editText)
            setPositiveButton(
                "OK"
            ) { _, _ ->
                viewModel.setIntent(KitsListFragmentContract.Intent.AddKit(editText.text.toString()))
            }
            show()
        }

    }

    private fun collectState() = lifecycleScope.launch {
        viewModel.state.collect { state ->
            binding.run {
                noKitsText.isVisible = state.items.isEmpty()
                kitsAdapter.submitList(state.items)
                binding.progress.isVisible = state.loading
            }
        }
    }

    private fun collectEffects() = lifecycleScope.launch {
        viewModel.effect.collect { effect ->
            when (effect) {
                is KitsListFragmentContract.Effect.NavigateToDetails -> {
                    findNavController().navigate(KitsListFragmentDirections.toAidKitDetails(effect.kitName))
                }
                is KitsListFragmentContract.Effect.Error -> {
                    Snackbar.make(
                        requireView(),
                        "Error: ${effect.cause.message}.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}