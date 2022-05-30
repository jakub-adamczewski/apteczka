package com.example.apteczka.db

import android.util.Log
import com.example.apteczka.auth.api.AuthManager
import com.example.apteczka.data.PREDEFINED_ACCESSORIES_IDS
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudFirestore @Inject constructor(
    private val authManager: AuthManager
) {

    private val _kitsState: MutableStateFlow<State> = MutableStateFlow(
        State(kits = emptyList())
    )
    val kitsState: Flow<State> = _kitsState


    private val db = FirebaseFirestore.getInstance()

    private val kitsCollectionReference: CollectionReference =
        db.collection("users")
            .document(authManager.currentUser!!.uid)
            .collection("kits")

    private fun getKitDocumentReference(kitName: String): DocumentReference =
        kitsCollectionReference.document(kitName)

    fun addKit(kitName: String) {
        getKitDocumentReference(kitName).set(
            Kit(
                name = kitName
            )
        )
            .addOnSuccessListener {
                Log.d("Test__", "success: $it")
                getKits()
            }
            .addOnFailureListener {
                Log.d("Test__", "failure: $it")
            }
    }

    fun updateKit(kit: Kit) {
        getKitDocumentReference(kit.name).set(kit)
            .addOnSuccessListener {
                Log.d("Test__", "success: $it")
            }
            .addOnFailureListener {
                Log.d("Test__", "failure: $it")
            }
    }

    fun getKits() {
        kitsCollectionReference.get()
            .addOnSuccessListener { allKitsSnapshot ->
                _kitsState.value =
                    State(
                        kits = allKitsSnapshot.documents.map {
                            it.toObject(FirestoreKit::class.java)!!.toKit()
                        }
                    )

            }
            .addOnFailureListener {
                Log.d("Test__", "failure: $it")
            }
    }

    private class FirestoreKit {
        lateinit var name: String
        lateinit var accessories: List<FirestoreDbAccessory>

        fun toKit(): Kit = Kit(name, accessories.map { it.toAccessory() })

    }

    private class FirestoreDbAccessory {
        var predefinedAccessoryId: Int = -1
        var validityDate: LocalDate? = null
        var ownedQuantity: Int = -1

        fun toAccessory(): DbAccessory =
            DbAccessory(predefinedAccessoryId, validityDate, ownedQuantity)
    }

    data class Kit(
        val name: String,
        val accessories: List<DbAccessory> = PREDEFINED_ACCESSORIES_IDS.map {
            DbAccessory(
                predefinedAccessoryId = it
            )
        }
    )

    data class DbAccessory(
        val predefinedAccessoryId: Int,
        val validityDate: LocalDate? = null,
        val ownedQuantity: Int = 0
    )

    data class State(
        val kits: List<Kit>
    )


}