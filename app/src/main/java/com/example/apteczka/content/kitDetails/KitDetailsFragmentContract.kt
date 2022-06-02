package com.example.apteczka.content.kitDetails

import com.example.apteczka.content.kitDetails.list.KitAccessoryAdapterItem
import java.time.LocalDate

object KitDetailsFragmentContract {

    data class State(
        val items: List<KitAccessoryAdapterItem>,
        val loading: Boolean
    )

    sealed class Effect {
        data class OpenDatePicker(val accessoryName: String) : Effect()
        data class OpenNumberPicker(val accessoryName: String, val maxValue: Int) : Effect()
        data class Error(val cause: Exception) : Effect()
    }

    sealed class Intent {
        data class OnDateClicked(val accessoryName: String) : Intent()
        data class OnDatePicked(val accessoryName: String, val date: LocalDate) : Intent()
        data class OnOwnedQuantityClicked(val accessoryName: String, val requiredCount: Int) : Intent()
        data class OnOwnedQuantityPicked(val accessoryName: String, val quantity: Int) : Intent()
    }

    val INITIAL_STATE = State(items = emptyList(), loading = false)


}