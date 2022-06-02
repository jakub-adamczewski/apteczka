package com.example.apteczka.db

import com.example.apteczka.auth.api.AuthManager
import com.example.apteczka.data.PREDEFINED_ACCESSORIES
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudFirestore @Inject constructor(
    private val authManager: AuthManager
) {

    private val _kitsState: MutableStateFlow<State> = MutableStateFlow(
        State(kits = emptyList(), loading = false)
    )
    val kitsState: Flow<State> = _kitsState

    private val _error: MutableSharedFlow<Error> = MutableSharedFlow()
    val error: Flow<Error> = _error

    private val db = FirebaseFirestore.getInstance()

    private val kitsCollectionReference: CollectionReference
        get() = db.collection("users")
            .document(authManager.currentUser!!.uid)
            .collection("kits")

    private fun emitLoading() {
        _kitsState.value = _kitsState.value.copy(loading = true)
    }

    private fun emitError(cause: Exception) {
        _kitsState.value = _kitsState.value.copy(loading = false)
        _error.tryEmit(Error(cause))
    }

    private fun getKitDocumentReference(kitName: String): DocumentReference =
        kitsCollectionReference.document(kitName)

    fun addKit(kitName: String) {
        emitLoading()
        getKitDocumentReference(kitName)
            .set(
                Kit(
                    name = kitName
                ).toFirestoreKit()
            )
            .addOnSuccessListener {
                emitKits()
            }
            .addOnFailureListener {
                emitError(it)
            }
    }

    private fun updateKit(kit: Kit) {
        emitLoading()
        getKitDocumentReference(kit.name).set(kit.toFirestoreKit())
            .addOnSuccessListener {
                emitKits()
            }
            .addOnFailureListener {
                emitError(it)
            }
    }

    fun updateAccessoryDate(kitName: String, accessoryName: String, newDate: LocalDate) {
        val currentAccessories = _kitsState.value.kits.find { it.name == kitName }!!.accessories
        val newAccessories = currentAccessories.map {
            if (PREDEFINED_ACCESSORIES[it.predefinedAccessoryId].name == accessoryName) {
                it.copy(validityDate = newDate)
            } else {
                it
            }
        }
        updateKit(
            Kit(
                name = kitName,
                accessories = newAccessories
            )
        )
    }

    fun updateAccessoryOwnedCount(
        kitName: String,
        accessoryName: String,
        accessoryOwnedCount: Int
    ) {
        val currentAccessories = _kitsState.value.kits.find { it.name == kitName }!!.accessories
        val newAccessories = currentAccessories.map {
            if (PREDEFINED_ACCESSORIES[it.predefinedAccessoryId].name == accessoryName) {
                it.copy(ownedQuantity = accessoryOwnedCount)
            } else {
                it
            }
        }
        updateKit(
            Kit(
                name = kitName,
                accessories = newAccessories
            )
        )
    }

    fun emitKits() {
        emitLoading()
        kitsCollectionReference.get()
            .addOnSuccessListener { allKitsSnapshot ->
                _kitsState.value =
                    State(
                        kits = allKitsSnapshot.documents.map {
                            it.toObject(FirestoreKit::class.java)!!.toKit()
                        },
                        loading = false
                    )

            }
            .addOnFailureListener {
                emitError(it)
            }
    }

    private class FirestoreKit {
        lateinit var name: String
        lateinit var accessories: List<FirestoreDbAccessory>

        fun toKit(): Kit = Kit(name, accessories.map { it.toAccessory() })

    }

    private class FirestoreDbAccessory {
        var predefinedAccessoryId: Int = -1
        var validityYear: Int? = null
        var validityMonth: Int? = null
        var validityDayOfMonth: Int? = null
        var ownedQuantity: Int = -1

        fun toAccessory(): DbAccessory =
            DbAccessory(
                predefinedAccessoryId,
                if (validityYear != null && validityMonth != null && validityDayOfMonth != null) {
                    LocalDate.of(validityYear!!, validityMonth!!, validityDayOfMonth!!)
                } else null,
                ownedQuantity
            )
    }

    private fun Kit.toFirestoreKit(): FirestoreKit = FirestoreKit().apply {
        name = this@toFirestoreKit.name
        accessories = this@toFirestoreKit.accessories.map {
            it.toFirestoreAccessory()
        }
    }

    private fun DbAccessory.toFirestoreAccessory(): FirestoreDbAccessory =
        FirestoreDbAccessory().apply {
            predefinedAccessoryId = this@toFirestoreAccessory.predefinedAccessoryId
            ownedQuantity = this@toFirestoreAccessory.ownedQuantity
            validityYear = this@toFirestoreAccessory.validityDate?.year
            validityMonth = this@toFirestoreAccessory.validityDate?.monthValue
            validityDayOfMonth = this@toFirestoreAccessory.validityDate?.dayOfMonth
        }

    data class Kit(
        val name: String,
        val accessories: List<DbAccessory> = PREDEFINED_ACCESSORIES.mapIndexed { index, _ ->
            DbAccessory(
                predefinedAccessoryId = index
            )
        }
    )

    data class DbAccessory(
        val predefinedAccessoryId: Int,
        val validityDate: LocalDate? = null,
        val ownedQuantity: Int = 0
    )

    data class State(
        val kits: List<Kit>,
        val loading: Boolean
    )

    data class Error(
        val cause: Exception
    )


}