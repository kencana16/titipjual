package com.kencana.titipjual.ui.penjualan.pembayaran

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kencana.titipjual.R
import com.kencana.titipjual.databinding.FragmentPembayaranBinding
import com.kencana.titipjual.ui.penjualan.detail.DetailPenjualanViewModel
import com.kencana.titipjual.utils.MinMaxFilter
import com.kencana.titipjual.utils.decimalFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PembayaranPenjualanFragment : Fragment() {
    private lateinit var pembayaranAdapter: PembayaranPenjualanAdapter
    private var _binding: FragmentPembayaranBinding? = null
    private val binding: FragmentPembayaranBinding get() = _binding!!

    private val detailPenjualanViewModel: DetailPenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPembayaranBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()
        return binding.root
    }

    private fun setupUI() {
        binding.recyclerview.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            pembayaranAdapter = PembayaranPenjualanAdapter()
            this.adapter = pembayaranAdapter
        }

        binding.btnAdd.apply {
            isEnabled = binding.tietUang.text.toString().isNotEmpty()
            setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Pembayaran")
                    .setMessage("Pastikan pembayaran sudah sesuai, data yang telah dimasukkan hanya dapat di edit melalui dashboard admin website")
                    .setPositiveButton("Benar") { _, _ ->
                        detailPenjualanViewModel.addPayment(binding.tietUang.text.toString())
                    }
                    .setNegativeButton("Kembali") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun updateUI() {
        detailPenjualanViewModel.penjualan.observe(viewLifecycleOwner) {
            val harusDibayar: String = it.total ?: "0"
            val sudahDibayar: String = it.dibayar ?: "0"
            val kekurangan = harusDibayar.toLong() - sudahDibayar.toLong()
            binding.tvHarusDibayar.text =
                getString(R.string.rp, decimalFormat.format(harusDibayar.toLong()))
            binding.tvSudahDibayar.text =
                getString(R.string.rp, decimalFormat.format(sudahDibayar.toLong()))
            binding.tvKekurangan.text =
                getString(R.string.rp, decimalFormat.format(kekurangan))

            binding.btnAdd.isEnabled = kekurangan > 0
            binding.tietUang.isEnabled = kekurangan > 0
            binding.tietUang.text = null
            binding.tietUang.filters = arrayOf<InputFilter>(
                MinMaxFilter(
                    0, kekurangan.toInt()
                )
            )

            pembayaranAdapter.submitList(it.detailPembayaran)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "PembayaranFragment"
    }
}
