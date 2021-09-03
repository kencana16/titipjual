package com.kencana.titipjual.ui.penjualan.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kencana.titipjual.databinding.ItemTextBinding
import javax.inject.Inject

class DateAdapter @Inject constructor() :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private val list = listOf<String>(
        TEXT_HARI_INI,
        TEXT_KEMARIN,
        TEXT_MINGGU_INI,
        TEXT_BULAN_INI,
        TEXT_PILIH_TANGGAL,
        TEXT_PILIH_RENTANG
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(list.get(position), onItemClickCallback)
    }

    class DateViewHolder private constructor(val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String, onItemClickCallback: OnItemClickCallback) {
            binding.text.text = data
            binding.root.setOnClickListener {
                onItemClickCallback.onClick(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): DateViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTextBinding.inflate(layoutInflater, parent, false)
                return DateViewHolder(binding)
            }
        }
    }

    fun setOnItemClickListener(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClick(item: String)
    }

    override fun getItemCount(): Int = list.size

    companion object {
        val TEXT_HARI_INI = "Hari ini"
        val TEXT_KEMARIN = "Kemarin"
        val TEXT_MINGGU_INI = "Minggu ini"
        val TEXT_BULAN_INI = "Bulan ini"
        val TEXT_PILIH_TANGGAL = "Pilih tanggal sendiri"
        val TEXT_PILIH_RENTANG = "Pilih rentang tanggal"
    }
}
