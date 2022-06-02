package com.example.apteczka.content.kitDetails.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.apteczka.R
import com.example.apteczka.databinding.KitAccessoryListItemBinding

class KitAccessoryViewHolder(
    private val binding: KitAccessoryListItemBinding,
    private val onDateClicked: (String) -> Unit,
    private val onCountClicked: (String, Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    constructor(
        parent: ViewGroup,
        onDateClicked: (String) -> Unit,
        onCountClicked: (String, Int) -> Unit
    ) : this(
        binding = KitAccessoryListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onDateClicked = onDateClicked,
        onCountClicked = onCountClicked
    )

    fun bind(item: KitAccessoryAdapterItem) {
        binding.run {
            image.setImageDrawable(
                AppCompatResources.getDrawable(
                    root.context,
                    item.predefinedAccessory.iconRes
                )
            )
            name.text = item.predefinedAccessory.name
            date.text =
                root.context.getString(R.string.validity_date, item.validityDate?.toString() ?: "ustaw!")
            requiredCountTitle.text =
                root.context.getString(
                    R.string.required_count,
                    item.predefinedAccessory.requiredQuantity
                )
            ownedCountTitle.text =
                root.context.getString(R.string.owned_count, item.ownedQuantity)

            date.setOnClickListener {
                onDateClicked(item.predefinedAccessory.name)
            }
            ownedCountTitle.setOnClickListener {
                onCountClicked(item.predefinedAccessory.name, item.predefinedAccessory.requiredQuantity)
            }
        }
    }
}