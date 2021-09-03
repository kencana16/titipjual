package com.kencana.titipjual.ui.penjualan.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailBarangItemPenjualan
import com.kencana.titipjual.data.response.PenjualanItem
import com.kencana.titipjual.databinding.FragmentDetailPenjualanBinding
import com.kencana.titipjual.ui.penjualan.home.PenjualanViewModel
import com.kencana.titipjual.utils.decimalFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPenjualanFragment : Fragment() {
    private var _binding: FragmentDetailPenjualanBinding? = null
    private val binding: FragmentDetailPenjualanBinding get() = _binding!!

    private val args: DetailPenjualanFragmentArgs by navArgs()
    private val detailPenjualanViewModel: DetailPenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private val penjualanViewModel: PenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPenjualanBinding.inflate(inflater, container, false)

        val item: PenjualanItem = args.penjualan
        if (detailPenjualanViewModel.penjualan.value?.idPenjualan != item.idPenjualan) {
            detailPenjualanViewModel.setData(item)
        }

        setupUI()
        updateUI()
        return binding.root
    }

    private fun setupUI() {

        binding.btnEdit.setOnClickListener {
            binding.btnEdit.visibility = View.GONE
            binding.btnSimpan.visibility = View.VISIBLE
            binding.btnReset.visibility = View.VISIBLE

            productAdapter.setEditable(true)
        }
        binding.btnSimpan.setOnClickListener {
            binding.btnEdit.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.GONE
            binding.btnReset.visibility = View.GONE

            productAdapter.setEditable(false)
            detailPenjualanViewModel.saveProduct()
        }
        binding.btnReset.setOnClickListener {
            binding.btnEdit.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.GONE
            binding.btnReset.visibility = View.GONE

            detailPenjualanViewModel.resetData()
            productAdapter.setEditable(false)
        }

        binding.rvProduct.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            productAdapter = ProductAdapter()
            this.adapter = productAdapter
            productAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickCallback {
                override fun update(item: DetailBarangItemPenjualan) {
                    detailPenjualanViewModel.updateProduct(item)
                }
            })
        }

        binding.viewgroupPembayaran.setOnClickListener {
            findNavController().navigate(
                DetailPenjualanFragmentDirections.actionDetailPenjualanFragmentToPembayaranFragment()
            )
        }
    }

    private fun updateUI() {
        detailPenjualanViewModel.penjualan.observe(viewLifecycleOwner) {
            penjualanViewModel.refresh()
            Log.d(TAG, "updateUI: $it")

            binding.tvSeller.text = it.namaPenjual
            binding.tvDate.text = it.tglPenjualan

            val total: String = decimalFormat.format(it.total?.toInt())
            val dibayar: String = decimalFormat.format(it.dibayar?.toInt())
            binding.tvTotalS.text = getString(R.string.total_s, total)
            binding.tvDibayar.text = getString(R.string.rp, dibayar)

            if (it.dibayar!!.toInt() < it.total!!.toInt() || it.dibayar.toInt() == 0) {
                binding.tvStatus.text = "Belum lunas"
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.design_default_color_error
                    )
                )
            } else {
                binding.tvStatus.text = "Lunas"
            }

            productAdapter.submitList(it.detailBarang)

            val sellerPhone = it.noHp
            binding.btnChatSeller.setOnClickListener {
                val url = "https://wa.me/62${sellerPhone?.drop(1)}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "DetailPenjualanFragment"
    }
}
