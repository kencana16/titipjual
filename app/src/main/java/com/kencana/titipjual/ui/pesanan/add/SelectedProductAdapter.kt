package com.kencana.titipjual.ui.pesanan.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailBarangItemPesanan
import com.kencana.titipjual.databinding.ItemBarangPesananBinding
import com.kencana.titipjual.utils.decimalFormat
import java.util.*
import javax.inject.Inject

class SelectedProductAdapter @Inject constructor() :
    ListAdapter<DetailBarangItemPesanan, SelectedProductAdapter.ProductViewHolder>(ProductDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var disableDelete: Boolean = false

    fun setDisabledButtonDelete(boolean: Boolean) {
        disableDelete = boolean
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback, disableDelete)
    }

    class ProductViewHolder private constructor(
        val context: Context,
        val binding: ItemBarangPesananBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: DetailBarangItemPesanan,
            onItemClickCallback: OnItemClickCallback,
            disableDelete: Boolean
        ) {
            binding.tvNamaBarang.text = data.namaBarang?.capitalize(Locale.ROOT)
            binding.tvJumlahBarang.text = "Jumlah : ${data.jmlBarang}"
            binding.tvHargaSatuan.text =
                context.getString(R.string.rp, decimalFormat.format(data.hargaSatuan?.toLong()))
            binding.tvSubtotal.text =
                context.getString(R.string.rp, decimalFormat.format(data.subtotal?.toLong()))
            binding.btnDelete.setOnClickListener {
                onItemClickCallback.onDelete(data)
            }

            if (disableDelete) {
                binding.btnDelete.visibility = View.GONE
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBarangPesananBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(parent.context, binding)
            }
        }
    }

    class ProductDiff() : DiffUtil.ItemCallback<DetailBarangItemPesanan>() {
        override fun areItemsTheSame(
            oldItem: DetailBarangItemPesanan,
            newItem: DetailBarangItemPesanan
        ): Boolean {
            return oldItem.idBarang == newItem.idBarang
        }

        override fun areContentsTheSame(
            oldItem: DetailBarangItemPesanan,
            newItem: DetailBarangItemPesanan
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onDelete(item: DetailBarangItemPesanan)
    }
}
