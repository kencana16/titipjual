package com.kencana.titipjual.ui.penjual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.databinding.ItemMasterBinding
import javax.inject.Inject

class PenjualAdapter @Inject constructor() :
    ListAdapter<PenjualItem, PenjualAdapter.BarangViewHolder>(BarangDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        return BarangViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class BarangViewHolder private constructor(val binding: ItemMasterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PenjualItem, onItemClickCallback: OnItemClickCallback) {
            binding.tvTitle.text = item.namaPenjual
            binding.tvSubtitle.text = "HP : ${item.noHp}"

            binding.root.setOnClickListener { onItemClickCallback.onClick(item) }
        }

        companion object {
            fun from(parent: ViewGroup): BarangViewHolder {
                val binding =
                    ItemMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return BarangViewHolder(binding)
            }
        }
    }

    class BarangDiff() : DiffUtil.ItemCallback<PenjualItem>() {
        override fun areItemsTheSame(oldItem: PenjualItem, newItem: PenjualItem): Boolean {
            return oldItem.idPenjual == newItem.idPenjual
        }

        override fun areContentsTheSame(oldItem: PenjualItem, newItem: PenjualItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: PenjualItem)
    }
}
