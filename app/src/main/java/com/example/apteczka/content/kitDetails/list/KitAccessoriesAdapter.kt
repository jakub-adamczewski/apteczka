package com.example.apteczka.content.kitDetails.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


class KitAccessoriesAdapter(
    private val kitAccessoryViewHolderFactory: (ViewGroup) -> KitAccessoryViewHolder
) : ListAdapter<KitAccessoryAdapterItem, KitAccessoryViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitAccessoryViewHolder =
        kitAccessoryViewHolderFactory(parent)

    override fun onBindViewHolder(holder: KitAccessoryViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<KitAccessoryAdapterItem>() {
            override fun areItemsTheSame(
                oldItem: KitAccessoryAdapterItem,
                newItem: KitAccessoryAdapterItem
            ): Boolean = oldItem.predefinedAccessory.name == newItem.predefinedAccessory.name

            override fun areContentsTheSame(
                oldItem: KitAccessoryAdapterItem,
                newItem: KitAccessoryAdapterItem
            ): Boolean = oldItem == newItem
        }
    }
}