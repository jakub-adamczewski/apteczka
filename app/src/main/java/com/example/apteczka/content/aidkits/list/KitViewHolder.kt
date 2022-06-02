package com.example.apteczka.content.aidkits.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apteczka.data.CountState
import com.example.apteczka.data.DateState
import com.example.apteczka.databinding.KitListItemBinding

class KitViewHolder(
    private val binding: KitListItemBinding,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, onItemClicked: (String) -> Unit) : this(
        binding = KitListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onItemClicked = onItemClicked
    )

    fun bind(item: KitAdapterItem) {
        binding.run {
            name.text = item.name
            root.setOnClickListener {
                onItemClicked(item.name)
            }
            quantityInfo.text = when(item.countState) {
                CountState.TO_LESS -> "Uzupełnij apteczkę."
                CountState.OK -> "Apteczka uzupełniona."
            }
            dateInfo.text = when(item.dateState) {
                DateState.NOT_SET -> "Ustaw daty przydatności."
                DateState.OUTDATED -> "Część apteczki przeterminowana."
                DateState.NEARLY_OUTDATED -> "Miesiąc do przeterminowania części apteczki."
                DateState.OK -> "Odpowiednie daty przydatności."
            }

        }
    }
}