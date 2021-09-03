package com.kencana.titipjual.ui.penjualan.add

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.response.DetailBarangItemPenjualan
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.databinding.FragmentAddPenjualanBinding
import com.kencana.titipjual.ui.penjualan.home.PenjualanViewModel
import com.kencana.titipjual.ui.penjualan.home.adapter.SellerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPenjualanFragment : Fragment() {

    private val penjualanViewModel: PenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private val addPenjualanViewModel: AddPenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private var _binding: FragmentAddPenjualanBinding? = null
    private val binding: FragmentAddPenjualanBinding
        get() = _binding!!

    private lateinit var sellerAdapter: SellerAdapter
    private lateinit var selectedAdapter: SelectedProductAdapter
    private lateinit var sellerSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPenjualanBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()

        return binding.root
    }

    private fun showSeller() {
        sellerSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupUI() {
        binding.tvDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                datePicker()
            }
        }

        binding.tvSeller.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                penjualanViewModel.getSeller()
                showSeller()
            }
        }

        binding.btnAddBarang.setOnClickListener {
            findNavController().navigate(AddPenjualanFragmentDirections.actionAddPenjualanFragmentToSelectBarangFragment())
        }

        binding.btnSimpan.setOnClickListener {
            addPenjualanViewModel.save()
            binding.progressCircular.visibility = View.VISIBLE
        }

        binding.recyclerviewDetailBarang.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            selectedAdapter = SelectedProductAdapter()
            this.adapter = selectedAdapter
            selectedAdapter.setOnItemClickListener(object :
                    SelectedProductAdapter.OnItemClickCallback {
                    override fun onDelete(item: DetailBarangItemPenjualan) {
                        Toast.makeText(
                            requireContext(),
                            "${item.namaBarang} dihapus",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        addPenjualanViewModel.removeProduct(item)
                    }
                })
        }

        sellerSheet = BottomSheetBehavior.from(binding.bottomSheetSeller)
        sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
        binding.rvBottomSheetSeller.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            sellerAdapter = SellerAdapter()
            this.adapter = sellerAdapter
            sellerAdapter.setOnItemClickListener(object : SellerAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualItem) {
                    addPenjualanViewModel.setSeller(item)
                    sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })
        }
    }

    private fun updateUI() {
        addPenjualanViewModel.penjualanItem.observe(viewLifecycleOwner) {
            if (!it.tglPenjualan.isNullOrEmpty()) {
                val date = it.tglPenjualan.split("-").asReversed().toTypedArray().joinToString("/")
                binding.tvDate.setText(date)
            } else {
                binding.tvDate.text = null
            }

            selectedAdapter.submitList(it.detailBarang)
            Log.d(TAG, "updateUI: penjualanItem\n ${it.detailBarang?.size}")

            binding.btnSimpan.isEnabled =
                (!it.tglPenjualan.isNullOrEmpty()) && (!it.idPenjual.isNullOrEmpty()) && (!it.detailBarang.isNullOrEmpty())
        }

        addPenjualanViewModel.seller.observe(viewLifecycleOwner) {
            binding.tvSeller.setText(it.namaPenjual)
        }

        penjualanViewModel.sellerList.observe(viewLifecycleOwner) {
            sellerAdapter.submitList(it)
        }

        addPenjualanViewModel.response.observe(viewLifecycleOwner) {
            binding.progressCircular.visibility = View.GONE
            Log.d(TAG, "updateUI: $it")
            if (it is Resource.Success && it.value.message.equals("Data created")) {
                penjualanViewModel.getData()
                addPenjualanViewModel.reset()

                AlertDialog.Builder(requireContext())
                    .setMessage("Penjualan dibuat")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    fun datePicker() {
        // Date Picker
        val builder =
            MaterialDatePicker.Builder.datePicker().setTitleText("Pilih tanggal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            Log.d(
                "DatePicker Activity",
                "Date String = ${picker.headerText}::  Date epoch values::$it"
            )
            addPenjualanViewModel.setDate("$it")
            binding.tvDate.clearFocus()
        }
        picker.addOnCancelListener { binding.tvDate.clearFocus() }
        picker.addOnDismissListener { binding.tvDate.clearFocus() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AddPenjualanFragment"
    }
}
