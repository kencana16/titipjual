package com.kencana.titipjual.ui.penjualan.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.databinding.FragmentSelectBarangBinding
import com.kencana.titipjual.utils.afterTextChanged
import com.kencana.titipjual.utils.getIndoDay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectBarangPenjualanFragment : Fragment() {
    private val addPenjualanViewModel: AddPenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private val selectBarangViewModel: SelectBarangViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private var _binding: FragmentSelectBarangBinding? = null
    private val binding: FragmentSelectBarangBinding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectBarangBinding.inflate(inflater, container, false)

        reset()
        setupUI()
        updateUI()

        return binding.root
    }

    private fun showSeller() {
        productSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun reset() {
        selectBarangViewModel.reset()
        binding.btnSimpan.isEnabled = false
    }

    private fun setupUI() {
        binding.tvProduct.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectBarangViewModel.getList()
                showSeller()
            }
        }

        binding.tvJmlBarang.afterTextChanged {
            submitForm()
        }

        binding.btnSimpan.setOnClickListener {
            addPenjualanViewModel.addProduct(
                selectBarangViewModel.selectedProduct.value!!,
                selectBarangViewModel.qty.value!!
            )
            findNavController().navigate(SelectBarangPenjualanFragmentDirections.actionSelectBarangFragmentToAddPenjualanFragment())
        }

        productSheet = BottomSheetBehavior.from(binding.bottomSheetProduct)
        productSheet.state = BottomSheetBehavior.STATE_HIDDEN
        binding.rvBottomSheetProduct.apply {
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
                override fun onClick(item: ProdukItem) {
                    selectBarangViewModel.setSelected(item)
                    productSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    submitForm()
                }
            })
        }
    }

    private fun submitForm() {
        selectBarangViewModel.submitForm(binding.tvJmlBarang.text.toString())
    }

    private fun updateUI() {
        selectBarangViewModel.productFormState.observe(viewLifecycleOwner) {

            binding.btnSimpan.isEnabled = it.isDataValid

            binding.tietProduct.error = it.namaError
            binding.tietJmlBarang.error = it.qtyError
        }

        selectBarangViewModel.productList.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                productAdapter.submitList(it.value.data?.produk)
            }
        }

        selectBarangViewModel.selectedProduct.observe(viewLifecycleOwner) {
            binding.tvProduct.setText(it.namaBarang)

            if ((!it.idBarang.isNullOrEmpty()) && (!addPenjualanViewModel.seller.value?.idPenjual.isNullOrEmpty())) {
                selectBarangViewModel.getRating(
                    it.idBarang,
                    addPenjualanViewModel.seller.value?.idPenjual!!
                )
            }
        }

        selectBarangViewModel.rating.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                binding.tvPenjualanHarian.text =
                    getString(R.string.rata_rata_penjualan_perhari_s, it.value.data?.harian)
                binding.tvPenjualanMingguan.text =
                    getString(
                        R.string.rata_rata_penjualan_setiap_s_s,
                        getIndoDay(it.value.data?.hari),
                        it.value.data?.mingguan
                    )

                Log.d(Companion.TAG, "updateUI: rating\n ${it.value}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "SelectBarangFragment"
    }
}
