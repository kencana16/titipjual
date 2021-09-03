package com.kencana.titipjual.ui.penjualan.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.data.response.DetailBarangItemPenjualan
import com.kencana.titipjual.databinding.ItemBarangPenjualanBinding
import javax.inject.Inject

class SelectedProductAdapter @Inject constructor() :
    ListAdapter<DetailBarangItemPenjualan, SelectedProductAdapter.ProductViewHolder>(ProductDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class ProductViewHolder private constructor(val binding: ItemBarangPenjualanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DetailBarangItemPenjualan, onItemClickCallback: OnItemClickCallback) {
            binding.tvNamaBarang.text = data.namaBarang?.capitalize()
            binding.tvJumlahBarang.text = "Jumlah : ${data.jmlProduk}"
            binding.btnDelete.setOnClickListener {
                onItemClickCallback.onDelete(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBarangPenjualanBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

    class ProductDiff() : DiffUtil.ItemCallback<DetailBarangItemPenjualan>() {
        override fun areItemsTheSame(
            oldItem: DetailBarangItemPenjualan,
            newItem: DetailBarangItemPenjualan
        ): Boolean {
            return oldItem.idBarang == newItem.idBarang
        }

        override fun areContentsTheSame(
            oldItem: DetailBarangItemPenjualan,
            newItem: DetailBarangItemPenjualan
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onDelete(item: DetailBarangItemPenjualan)
    }
}
