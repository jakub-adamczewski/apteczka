package com.example.apteczka.content.aidkits

import com.example.apteczka.content.aidkits.list.KitAdapterItem

object KitsListFragmentContract {

    data class State(
        val items: List<KitAdapterItem>,
        val loading: Boolean
    )

    sealed class Effect {
        data class NavigateToDetails(val kitName: String) : Effect()
        data class Error(val cause: Exception) : Effect()
    }

    sealed class Intent {
        data class OnItemClicked(val name: String) : Intent()
        data class AddKit(val name: String) : Intent()
    }

    val INITIAL_STATE = State(
        items = emptyList(),
        loading = false
    )

}