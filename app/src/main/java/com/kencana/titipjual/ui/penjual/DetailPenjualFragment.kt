package com.kencana.titipjual.ui.penjual

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
import com.kencana.titipjual.databinding.FragmentDetailPenjualBinding
import com.kencana.titipjual.utils.TYPE

class DetailPenjualFragment : Fragment() {

    private val penjualViewModel: PenjualViewModel by hiltNavGraphViewModels(R.id.nav_penjual)
    private var _binding: FragmentDetailPenjualBinding? = null
    private val binding: FragmentDetailPenjualBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailPenjualBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()

        return binding.root
    }

    private fun setupUI() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                DetailPenjualFragmentDirections.actionDetailPenjualFragmentToFormPenjualFragment(
                    TYPE.edit
                )
            )
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Produk")
                .setMessage("Yakin ingin menghapus, data yang telah dihapus tidak dapat dikembalikan")
                .setPositiveButton("Yakin") { _, _ ->
                    penjualViewModel.deleteSelected()
                }
                .setNegativeButton("batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun updateUI() {
        penjualViewModel.selectedSeller.observe(viewLifecycleOwner) {
            if (it.idPenjual.isNullOrEmpty()) {
                findNavController().navigate(R.id.penjualFragment)
            }
            binding.apply {
                tvSellerName.text = it.namaPenjual
                tvSellerPhone.text = it.noHp
                tvSellerAddress.text = if (!it.alamat.isNullOrEmpty()) it.alamat else "-"

                tvDibuat.text = "Dibuat : ${it.dateCreated}"
                tvDiubah.text = "Diubah : ${it.dateModified}"
            }
        }
        penjualViewModel.sellerList.observe(viewLifecycleOwner) {

            if (it is Resource.Success && it.value.message.equals("Data deleted", true)) {
                Toast.makeText(requireContext(), "Data Terhapus", Toast.LENGTH_SHORT).show()
                penjualViewModel.resetSelectedSeller()
                penjualViewModel.resetMessageResponse()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
