package com.example.apteczka.content.kitDetails.list

import com.example.apteczka.data.PredefinedAccessory
import java.time.LocalDate

data class KitAccessoryAdapterItem(
    val predefinedAccessory: PredefinedAccessory,
    val validityDate: LocalDate?,
    val ownedQuantity: Int
)
