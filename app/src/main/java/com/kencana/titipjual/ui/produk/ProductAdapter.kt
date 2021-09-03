package com.kencana.titipjual.ui.produk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.databinding.ItemMasterBinding
import javax.inject.Inject

class ProductAdapter @Inject constructor() :
    ListAdapter<ProdukItem, ProductAdapter.BarangViewHolder>(BarangDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        return BarangViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class BarangViewHolder private constructor(val binding: ItemMasterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProdukItem, onItemClickCallback: OnItemClickCallback) {
            binding.tvTitle.text = item.namaBarang
            binding.tvSubtitle.text = "${
            binding.root.context.getString(
                R.string.harga_reseller_rp,
                item.hargaSatuanReseller
            )
            }\n${
            binding.root.context.getString(
                R.string.harga_normal_rp,
                item.hargaSatuanNormal
            )
            } "
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

    class BarangDiff() : DiffUtil.ItemCallback<ProdukItem>() {
        override fun areItemsTheSame(oldItem: ProdukItem, newItem: ProdukItem): Boolean {
            return oldItem.idBarang == newItem.idBarang
        }

        override fun areContentsTheSame(oldItem: ProdukItem, newItem: ProdukItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: ProdukItem)
    }
}
