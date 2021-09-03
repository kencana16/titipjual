package com.kencana.titipjual.ui.penjualan.pembayaran

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailPembayaranItemPenjualan
import com.kencana.titipjual.databinding.ItemRowBayarBinding
import com.kencana.titipjual.utils.decimalFormat
import javax.inject.Inject

class PembayaranPenjualanAdapter @Inject constructor() :
    ListAdapter<DetailPembayaranItemPenjualan, PembayaranPenjualanAdapter.PembayaranViewHolder>(
        PaymentDiff()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PembayaranViewHolder {
        return PembayaranViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PembayaranViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PembayaranViewHolder private constructor(
        val context: Context,
        val binding: ItemRowBayarBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: DetailPembayaranItemPenjualan
        ) {
            binding.tvNo.text = (layoutPosition + 1).toString()
            binding.tvWaktu.text = data.tanggal
            binding.tvJumlah.text =
                context.getString(R.string.rp, decimalFormat.format(data.jumlahUang?.toLong()))
        }

        companion object {
            fun from(parent: ViewGroup): PembayaranViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRowBayarBinding.inflate(layoutInflater, parent, false)
                return PembayaranViewHolder(parent.context, binding)
            }
        }
    }

    class PaymentDiff() : DiffUtil.ItemCallback<DetailPembayaranItemPenjualan>() {
        override fun areItemsTheSame(
            oldItem: DetailPembayaranItemPenjualan,
            newItem: DetailPembayaranItemPenjualan
        ): Boolean {
            return oldItem.idPembayaranPenjualan == newItem.idPembayaranPenjualan
        }

        override fun areContentsTheSame(
            oldItem: DetailPembayaranItemPenjualan,
            newItem: DetailPembayaranItemPenjualan
        ): Boolean {
            return oldItem == newItem
        }
    }
}
