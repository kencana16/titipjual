package com.kencana.titipjual.ui.penjualan.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.PenjualanItem
import com.kencana.titipjual.databinding.ItemPenjualanBinding
import javax.inject.Inject

class SalesAdapter @Inject constructor() :
    ListAdapter<PenjualanItem, SalesAdapter.SalesViewHolder>(SalesDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        return SalesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class SalesViewHolder private constructor(
        private val binding: ItemPenjualanBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PenjualanItem, onItemClickCallback: OnItemClickCallback) {
            binding.apply {
                tvNamaPenjual.text = data.namaPenjual
                tvJmlBarang.text = context.getString(R.string.jumlah_barang_s, data.jmlProduk)
                tvTerjual.text = context.getString(R.string.terjual_s, data.jmlTerjual)
                tvDate.text = data.tglPenjualan
            }
            binding.root.setOnClickListener {
                onItemClickCallback.onClick(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): SalesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPenjualanBinding.inflate(layoutInflater, parent, false)
                return SalesViewHolder(binding, parent.context)
            }
        }
    }

    class SalesDiff : DiffUtil.ItemCallback<PenjualanItem>() {
        override fun areItemsTheSame(oldItem: PenjualanItem, newItem: PenjualanItem): Boolean {
            return oldItem.idPenjualan == newItem.idPenjualan
        }

        override fun areContentsTheSame(oldItem: PenjualanItem, newItem: PenjualanItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: PenjualanItem)
    }
}
