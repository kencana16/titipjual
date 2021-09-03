package com.kencana.titipjual.ui.pesanan.add

import android.os.Bundle
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
import com.kencana.titipjual.ui.penjualan.add.ProductAdapter
import com.kencana.titipjual.ui.penjualan.add.SelectBarangViewModel
import com.kencana.titipjual.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectBarangPesananFragment : Fragment() {
    private val addPesananViewModel: AddPesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private val selectBarangViewModel: SelectBarangViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private var _binding: FragmentSelectBarangBinding? = null
    private val binding: FragmentSelectBarangBinding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
    }

    private fun setupUI() {
        binding.tvPenjualanMingguan.visibility = View.GONE
        binding.tvPenjualanHarian.visibility = View.GONE

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
            addPesananViewModel.addProduct(
                selectBarangViewModel.selectedProduct.value!!,
                selectBarangViewModel.qty.value!!
            )
            findNavController().navigateUp()
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
