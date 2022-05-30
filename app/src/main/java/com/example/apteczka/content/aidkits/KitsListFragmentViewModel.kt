package com.example.apteczka.content.aidkits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apteczka.content.aidkits.KitsListFragmentContract.Effect
import com.example.apteczka.content.aidkits.KitsListFragmentContract.INITIAL_STATE
import com.example.apteczka.content.aidkits.KitsListFragmentContract.Intent
import com.example.apteczka.content.aidkits.KitsListFragmentContract.State
import com.example.apteczka.content.aidkits.list.KitAdapterItem
import com.example.apteczka.data.ValidityState
import com.example.apteczka.db.CloudFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KitsListFragmentViewModel @Inject constructor(
    private val cloudFirestore: CloudFirestore
): ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state: Flow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            cloudFirestore.kitsState.collect { cloudState ->
                _state.emit(
                    _state.value.copy(
                        items = cloudState.kits.map { it.toKitAdapterItem() }
                    )
                )
            }
        }
    }

    fun setIntent(intent: Intent) = viewModelScope.launch {
        when (intent) {
            is Intent.OnItemClicked -> {
                _effect.send(Effect.NavigateToDetails(intent.name))
            }
            is Intent.AddKit -> cloudFirestore.addKit(intent.name)
        }
    }

    private fun CloudFirestore.Kit.toKitAdapterItem(): KitAdapterItem =
        KitAdapterItem(
            name = name,
            validityState = ValidityState.VALID
        )

}