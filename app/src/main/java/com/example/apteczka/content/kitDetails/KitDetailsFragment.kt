package com.example.apteczka.content.kitDetails

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apteczka.content.kitDetails.list.KitAccessoriesAdapter
import com.example.apteczka.content.kitDetails.list.KitAccessoryViewHolder
import com.example.apteczka.databinding.FragmentKitDetailsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*


@AndroidEntryPoint
class KitDetailsFragment : Fragment() {

    private val binding by lazy { FragmentKitDetailsBinding.inflate(layoutInflater) }
    private val viewModel: KitDetailsViewModel by viewModels()
    private val accessoriesAdapter = KitAccessoriesAdapter(
        kitAccessoryViewHolderFactory = {
            KitAccessoryViewHolder(
                parent = it,
                onDateClicked = { name ->
                    viewModel.setIntent(KitDetailsFragmentContract.Intent.OnDateClicked(name))
                },
                onCountClicked = { name, requiredCount ->
                    viewModel.setIntent(
                        KitDetailsFragmentContract.Intent.OnOwnedQuantityClicked(
                            accessoryName = name,
                            requiredCount = requiredCount
                        )
                    )
                }
            )
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
        accessoriesList.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = accessoriesAdapter
        }
    }


    private fun collectState() = lifecycleScope.launch {
        viewModel.state.collect { state ->
            accessoriesAdapter.submitList(state.items)
            binding.progress.isVisible = state.loading
        }
    }

    private fun collectEffects() = lifecycleScope.launch {
        viewModel.effect.collect { effect ->
            when (effect) {
                is KitDetailsFragmentContract.Effect.OpenDatePicker -> {
                    val currentDate = LocalDate.now()
                    DatePickerDialog(
                        requireContext(),
                        { _, year, month, day ->
                            viewModel.setIntent(
                                KitDetailsFragmentContract.Intent.OnDatePicked(
                                    accessoryName = effect.accessoryName,
                                    date = LocalDate.of(year, month + 1, day)
                                )
                            )
                        },
                        currentDate.year,
                        currentDate.monthValue,
                        currentDate.dayOfMonth
                    ).show()
                }
                is KitDetailsFragmentContract.Effect.OpenNumberPicker -> {
                    showNumberPickerDialog(
                        maxValue = effect.maxValue,
                        accessoryName = effect.accessoryName
                    )
                }
                is KitDetailsFragmentContract.Effect.Error -> {
                    Snackbar.make(
                        requireView(),
                        "Error: ${effect.cause.message}.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showNumberPickerDialog(maxValue: Int, accessoryName: String) {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 0
        numberPicker.maxValue = maxValue

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Wybierz posiadaną liczbę dla:")
        builder.setMessage(accessoryName)
        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            viewModel.setIntent(
                KitDetailsFragmentContract.Intent.OnOwnedQuantityPicked(
                    accessoryName = accessoryName,
                    quantity = numberPicker.value
                )
            )
            dialog.dismiss()
        }
        builder.setNegativeButton(
            "CANCEL"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setView(numberPicker)
        builder.create().show()
    }
}