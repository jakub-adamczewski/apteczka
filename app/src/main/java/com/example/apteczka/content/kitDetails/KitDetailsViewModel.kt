package com.example.apteczka.content.kitDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apteczka.content.kitDetails.KitDetailsFragmentContract.Effect
import com.example.apteczka.content.kitDetails.KitDetailsFragmentContract.INITIAL_STATE
import com.example.apteczka.content.kitDetails.KitDetailsFragmentContract.Intent
import com.example.apteczka.content.kitDetails.KitDetailsFragmentContract.State
import com.example.apteczka.content.kitDetails.list.KitAccessoryAdapterItem
import com.example.apteczka.data.PREDEFINED_ACCESSORIES
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
class KitDetailsViewModel @Inject constructor(
    private val cloudFirestore: CloudFirestore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val kitName by lazy { savedStateHandle.get<String>("kit_name")!! }

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state: Flow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            cloudFirestore.kitsState.collect { cloudState ->
                _state.emit(
                    _state.value.copy(
                        items = cloudState.kits
                            .find { it.name == kitName }
                            ?.accessories
                            ?.map { it.toAdapterItem() }
                            .orEmpty(),
                        loading = cloudState.loading
                    )
                )
            }
        }
        viewModelScope.launch {
            cloudFirestore.error.collect { error ->
                _effect.send(Effect.Error(error.cause))
            }
        }
    }

    fun setIntent(intent: Intent) = viewModelScope.launch {
        when (intent) {
            is Intent.OnDateClicked -> _effect.send(Effect.OpenDatePicker(intent.accessoryName))
            is Intent.OnOwnedQuantityClicked -> _effect.send(
                Effect.OpenNumberPicker(
                    intent.accessoryName,
                    intent.requiredCount
                )
            )
            is Intent.OnDatePicked -> cloudFirestore.updateAccessoryDate(
                kitName = kitName,
                accessoryName = intent.accessoryName,
                newDate = intent.date
            )
            is Intent.OnOwnedQuantityPicked -> cloudFirestore.updateAccessoryOwnedCount(
                kitName = kitName,
                accessoryName = intent.accessoryName,
                accessoryOwnedCount = intent.quantity
            )
        }
    }

    private fun CloudFirestore.DbAccessory.toAdapterItem(): KitAccessoryAdapterItem =
        KitAccessoryAdapterItem(
            predefinedAccessory = PREDEFINED_ACCESSORIES[predefinedAccessoryId],
            validityDate = validityDate,
            ownedQuantity = ownedQuantity
        )

}