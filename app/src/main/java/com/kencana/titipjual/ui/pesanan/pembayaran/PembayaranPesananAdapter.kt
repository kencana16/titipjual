package com.kencana.titipjual.ui.pesanan.pembayaran

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailPembayaranItemPesanan
import com.kencana.titipjual.databinding.ItemRowBayarBinding
import com.kencana.titipjual.utils.decimalFormat
import javax.inject.Inject

class PembayaranPesananAdapter @Inject constructor() :
    ListAdapter<DetailPembayaranItemPesanan, PembayaranPesananAdapter.PembayaranViewHolder>(
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
            data: DetailPembayaranItemPesanan
        ) {
            binding.tvNo.text = (layoutPosition + 1).toString()
            binding.tvWaktu.text = data.tanggal
            binding.tvJumlah.text = context.getString(R.string.rp, decimalFormat.format(data.jumlahUang?.toLong()))
        }

        companion object {
            fun from(parent: ViewGroup): PembayaranViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRowBayarBinding.inflate(layoutInflater, parent, false)
                return PembayaranViewHolder(parent.context, binding)
            }
        }
    }

    class PaymentDiff() : DiffUtil.ItemCallback<DetailPembayaranItemPesanan>() {
        override fun areItemsTheSame(
            oldItem: DetailPembayaranItemPesanan,
            newItem: DetailPembayaranItemPesanan
        ): Boolean {
            return oldItem.idPembayaranPesanan == newItem.idPembayaranPesanan
        }

        override fun areContentsTheSame(
            oldItem: DetailPembayaranItemPesanan,
            newItem: DetailPembayaranItemPesanan
        ): Boolean {
            return oldItem == newItem
        }
    }
}
