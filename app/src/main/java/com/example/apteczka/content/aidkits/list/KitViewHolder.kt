package com.example.apteczka.content.aidkits.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apteczka.content.aidkits.KitsListFragmentContract
import com.example.apteczka.content.util.getColor
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
//            root.setBackgroundColor(root.context.getColor(item.validityState.getColor()))
            root.setOnClickListener {
                onItemClicked(item.name)
            }
        }
    }
}