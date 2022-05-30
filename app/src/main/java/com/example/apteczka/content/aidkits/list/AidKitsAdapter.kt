package com.example.apteczka.content.aidkits.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


class AidKitsAdapter(
    private val kitViewHolderFactory: (ViewGroup) -> KitViewHolder
) : ListAdapter<KitAdapterItem, KitViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitViewHolder =
        kitViewHolderFactory(parent)

    override fun onBindViewHolder(holder: KitViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<KitAdapterItem>() {
            override fun areItemsTheSame(
                oldItem: KitAdapterItem,
                newItem: KitAdapterItem
            ): Boolean = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: KitAdapterItem,
                newItem: KitAdapterItem
            ): Boolean = oldItem == newItem

        }
    }
}