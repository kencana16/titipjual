package com.kencana.titipjual.ui.penjualan.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.databinding.ItemTextBinding
import com.kencana.titipjual.ui.penjualan.home.adapter.SellerAdapter.SellerViewHolder
import java.util.*
import javax.inject.Inject

class SellerAdapter @Inject constructor() :
    ListAdapter<PenjualItem, SellerViewHolder>(SellerDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        return SellerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class SellerViewHolder private constructor(val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PenjualItem, onItemClickCallback: OnItemClickCallback) {
            binding.text.text = data.namaPenjual?.capitalize(Locale.getDefault())
            binding.root.setOnClickListener {
                onItemClickCallback.onClick(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): SellerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTextBinding.inflate(layoutInflater, parent, false)
                return SellerViewHolder(binding)
            }
        }
    }

    class SellerDiff() : DiffUtil.ItemCallback<PenjualItem>() {
        override fun areItemsTheSame(oldItem: PenjualItem, newItem: PenjualItem): Boolean {
            return oldItem.idPenjual == newItem.idPenjual
        }

        override fun areContentsTheSame(oldItem: PenjualItem, newItem: PenjualItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: PenjualItem)
    }
}
