package com.kencana.titipjual.ui.penjualan.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.databinding.ItemTextBinding
import java.util.*
import javax.inject.Inject

class ProductAdapter @Inject constructor() :
    ListAdapter<ProdukItem, ProductAdapter.ProductViewHolder>(ProductDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class ProductViewHolder private constructor(val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ProdukItem, onItemClickCallback: OnItemClickCallback) {
            binding.text.text = data.namaBarang?.capitalize(Locale.getDefault())
            binding.root.setOnClickListener {
                onItemClickCallback.onClick(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTextBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

    class ProductDiff() : DiffUtil.ItemCallback<ProdukItem>() {
        override fun areItemsTheSame(oldItem: ProdukItem, newItem: ProdukItem): Boolean {
            return oldItem.idBarang == newItem.idBarang
        }

        override fun areContentsTheSame(oldItem: ProdukItem, newItem: ProdukItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: ProdukItem)
    }
}
