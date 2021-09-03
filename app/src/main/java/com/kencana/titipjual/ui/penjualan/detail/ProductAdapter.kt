package com.kencana.titipjual.ui.penjualan.detail

import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailBarangItemPenjualan
import com.kencana.titipjual.databinding.ItemRekapPenjualanBinding
import com.kencana.titipjual.utils.MinMaxFilter
import com.kencana.titipjual.utils.decimalFormat
import javax.inject.Inject

class ProductAdapter @Inject constructor() :
    ListAdapter<DetailBarangItemPenjualan, ProductAdapter.ProductViewHolder>(ProductDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var editable: Boolean = false

    fun setEditable(editable: Boolean) {
        this.editable = editable
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback, editable)
    }

    class ProductViewHolder private constructor(val binding: ItemRekapPenjualanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: DetailBarangItemPenjualan,
            onItemClickCallback: OnItemClickCallback,
            editable: Boolean
        ) {
            binding.editText.isEnabled = editable

            binding.tvProductName.text = data.namaBarang?.capitalize()
            binding.tvProductQty.text =
                binding.root.context.getString(R.string.jumlah_s, data.jmlProduk)
            binding.editText.setText(data.jmlTerjual)
            binding.labelHargaTotal.text =
                binding.root.context.getString(R.string.rp, decimalFormat.format(data.jmlUang?.toLong()))
            binding.labelHargaSatuan.text =
                binding.root.context.getString(
                    R.string.xrp,
                    decimalFormat.format(data.hargaSatuanReseller?.toLong())
                )

            binding.editText.filters = arrayOf<InputFilter>(
                MinMaxFilter(
                    0, data.jmlProduk!!.toInt()
                )
            )

            binding.editText.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    var s: String? = (view as EditText).text.toString().trim()
                    if (s == "") {
                        s = "0"
                    }
                    Log.d("TAG", "bind: $s")
                    onItemClickCallback.update(data.copy(jmlTerjual = s))
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRekapPenjualanBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }

    class ProductDiff : DiffUtil.ItemCallback<DetailBarangItemPenjualan>() {
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
        fun update(item: DetailBarangItemPenjualan)
    }
}
