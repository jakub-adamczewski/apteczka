package com.example.apteczka.content.aidkits

import android.icu.util.LocaleData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apteczka.content.aidkits.KitsListFragmentContract.Effect
import com.example.apteczka.content.aidkits.KitsListFragmentContract.INITIAL_STATE
import com.example.apteczka.content.aidkits.KitsListFragmentContract.Intent
import com.example.apteczka.content.aidkits.KitsListFragmentContract.State
import com.example.apteczka.content.aidkits.list.KitAdapterItem
import com.example.apteczka.content.kitDetails.KitDetailsFragmentContract
import com.example.apteczka.data.CountState
import com.example.apteczka.data.DateState
import com.example.apteczka.data.PREDEFINED_ACCESSORIES
import com.example.apteczka.db.CloudFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class KitsListFragmentViewModel @Inject constructor(
    private val cloudFirestore: CloudFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(INITIAL_STATE)
    val state: Flow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        cloudFirestore.emitKits()
        viewModelScope.launch {
            cloudFirestore.kitsState.collect { cloudState ->
                _state.emit(
                    _state.value.copy(
                        items = cloudState.kits.map { it.toKitAdapterItem() }
                    )
                )
            }
            viewModelScope.launch {
                cloudFirestore.error.collect { error ->
                    _effect.send(Effect.Error(error.cause))
                }
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
            dateState = when {
                accessories.any { it.validityDate == null } -> DateState.NOT_SET
                accessories.any { it.validityDate!!.minusMonths(1) < LocalDate.now() } -> DateState.NEARLY_OUTDATED
                accessories.any { it.validityDate!! < LocalDate.now() } -> DateState.OUTDATED
                else -> DateState.OK
            },
            countState = if (accessories.all { it.ownedQuantity == PREDEFINED_ACCESSORIES[it.predefinedAccessoryId].requiredQuantity }) {
                CountState.OK
            } else {
                CountState.TO_LESS
            }
        )

}