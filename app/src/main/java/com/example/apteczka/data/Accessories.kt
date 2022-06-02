package com.example.apteczka.data

import androidx.annotation.DrawableRes
import com.example.apteczka.R
import java.util.*

data class PredefinedAccessory(
    @DrawableRes val iconRes: Int,
    val requiredQuantity: Int,
    val name: String
)


val PREDEFINED_ACCESSORIES: List<PredefinedAccessory> = listOf(
    PredefinedAccessory(
        iconRes = R.drawable.instruction,
        requiredQuantity = 1,
        name = "instrukcja udzielania pierwszej pomocy z wykazem telefonów alarmowych"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.mouth,
        requiredQuantity = 1,
        name = "ustnik do sztucznego oddychania"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.tissue,
        requiredQuantity = 2,
        name = "chusteczka nasączona"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.blanket,
        requiredQuantity = 1,
        name = "koc ratunkowy 160 × 210 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.blanket,
        requiredQuantity = 4,
        name = "rękawice winylowe"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.scissors,
        requiredQuantity = 1,
        name = "nożyczki 14,5 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.blanket,
        requiredQuantity = 2,
        name = "chusta trójkątna"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.tissue,
        requiredQuantity = 6,
        name = "kompres 10 cm × 10 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.arm,
        requiredQuantity = 1,
        name = "chusta opatrunkowa 60 cm × 80 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.arm,
        requiredQuantity = 1,
        name = "chusta opatrunkowa 40 cm × 60 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.hand,
        requiredQuantity = 3,
        name = "opaska elastyczna 4 m × 8 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.hand,
        requiredQuantity = 2,
        name = "opaska elastyczna 4 m × 6 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.roll,
        requiredQuantity = 1,
        name = "przylepiec 5 m × 2,5 cm"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.plaster,
        requiredQuantity = 1,
        name = "zestaw plastrów"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.roll2,
        requiredQuantity = 1,
        name = "opatrunek indywidualny K"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.roll2,
        requiredQuantity = 2,
        name = "opatrunek indywidualny M"
    ),
    PredefinedAccessory(
        iconRes = R.drawable.roll2,
        requiredQuantity = 1,
        name = "opatrunek indywidualny G"
    ),
)
