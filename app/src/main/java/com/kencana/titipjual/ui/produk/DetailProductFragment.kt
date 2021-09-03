package com.kencana.titipjual.ui.produk

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.databinding.FragmentDetailProductBinding
import com.kencana.titipjual.utils.TYPE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private val produkViewModel: ProdukViewModel by hiltNavGraphViewModels(R.id.nav_product)

    private var _binding: FragmentDetailProductBinding? = null
    private val binding: FragmentDetailProductBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()
        return binding.root
    }

    private fun setupUI() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                DetailProductFragmentDirections.actionDetailProductFragmentToFormProductFragment(
                    TYPE.edit
                )
            )
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Produk")
                .setMessage("Yakin ingin menghapus, data yang telah dihapus tidak dapat dikembalikan")
                .setPositiveButton("Yakin") { _, _ ->
                    produkViewModel.deleteSelected()
                }
                .setNegativeButton("batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun updateUI() {
        produkViewModel.selectedProduct.observe(viewLifecycleOwner) {
            if (it.idBarang.isNullOrEmpty()) {
                findNavController().navigate(R.id.productFragment)
            }
            binding.apply {
                tvProductName.text = it.namaBarang
                tvSatuan.text = it.satuan
                tvNormalPrice.text = it.hargaSatuanNormal
                tvResellerPrice.text = it.hargaSatuanReseller

                tvDibuat.text = "Dibuat : ${it.dateCreated}"
                tvDiubah.text = "Diubah : ${it.dateModified}"
            }
        }
        produkViewModel.productList.observe(viewLifecycleOwner) {

            if (it is Resource.Success && it.value.message.equals("Data deleted", true)) {
                Toast.makeText(requireContext(), "Data Terhapus", Toast.LENGTH_SHORT).show()
                produkViewModel.resetSelectedProduct()
                produkViewModel.resetMessageResponse()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        produkViewModel.resetSelectedProduct()
        _binding = null
    }

    companion object {
        // TODO: 6/2/21
        // menambahkan rata rata penjalan seperti pada tampilan figma
        private const val TAG = "DetailProductFragment"
    }
}
