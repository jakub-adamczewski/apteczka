package com.example.apteczka.content.aidkits.list

import com.example.apteczka.data.CountState
import com.example.apteczka.data.DateState

data class KitAdapterItem(
    val name: String,
    val dateState: DateState,
    val countState: CountState
)
