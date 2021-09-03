package com.kencana.titipjual.ui.pesanan.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.PesananItem
import com.kencana.titipjual.databinding.ItemPesananBinding
import com.kencana.titipjual.utils.decimalFormat
import javax.inject.Inject

class PesananAdapter @Inject constructor() :
    ListAdapter<PesananItem, PesananAdapter.OrderViewHolder>(OrderDiff()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickCallback)
    }

    class OrderViewHolder private constructor(
        private val binding: ItemPesananBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PesananItem, onItemClickCallback: OnItemClickCallback) {
            binding.apply {
                tvNamaPemesan.text = data.namaPemesan
                tvDueDate.text = data.tglDiambil
                tvStatus.text = data.strStatus()
                tvNamaPemesan.text = data.namaPemesan
                tvHpPemesan.text = data.noHpPemesan

                var strDipesan = "${
                context.resources.getString(
                    R.string.rp,
                    decimalFormat.format(data.total?.toLong())
                )
                }\n"
                data.detailBarang?.forEach {
                    strDipesan += "${it?.namaBarang} * ${it?.jmlBarang} | "
                }
                tvBarangDipesan.text = strDipesan.dropLast(2)

                when (data.status) {
                    "0" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_1))
                    }
                    "1" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_2))
                    }
                    "2" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_3))
                    }
                    "3" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_4))
                    }
                    else -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_0))
                    }
                }
            }
            binding.root.setOnClickListener {
                onItemClickCallback.onClick(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): OrderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPesananBinding.inflate(layoutInflater, parent, false)
                return OrderViewHolder(binding, parent.context)
            }
        }
    }

    class OrderDiff : DiffUtil.ItemCallback<PesananItem>() {
        override fun areItemsTheSame(oldItem: PesananItem, newItem: PesananItem): Boolean {
            return oldItem.idPesanan == newItem.idPesanan
        }

        override fun areContentsTheSame(oldItem: PesananItem, newItem: PesananItem): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: PesananItem)
    }
}
