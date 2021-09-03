package com.kencana.titipjual.ui.produk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.databinding.FragmentFormProductBinding
import com.kencana.titipjual.utils.TYPE
import com.kencana.titipjual.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormProductFragment : Fragment() {
    private var _binding: FragmentFormProductBinding? = null
    private val binding: FragmentFormProductBinding get() = _binding!!
    private val args: FormProductFragmentArgs by navArgs()
    private val produkViewModel: ProdukViewModel by hiltNavGraphViewModels(R.id.nav_product)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormProductBinding.inflate(inflater, container, false)

        if (args.type == TYPE.add) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Add Barang"
            produkViewModel.resetSelectedProduct()
        } else if (args.type == TYPE.edit) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Barang"
            binding.etProductName.setText(produkViewModel.selectedProduct.value?.namaBarang)
            binding.etSatuan.setText(produkViewModel.selectedProduct.value?.satuan)
            binding.etNormalPrice.setText(produkViewModel.selectedProduct.value?.hargaSatuanNormal)
            binding.etResellerPrice.setText(produkViewModel.selectedProduct.value?.hargaSatuanReseller)
            submitForm()
        }

        setupUI()
        updateUI()

        return binding.root
    }

    private fun setupUI() {
        binding.etProductName.afterTextChanged {
            submitForm()
        }
        binding.etSatuan.afterTextChanged {
            submitForm()
        }
        binding.etNormalPrice.afterTextChanged {
            submitForm()
        }
        binding.etResellerPrice.afterTextChanged {
            submitForm()
        }

        binding.btnSimpan.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            produkViewModel.save()
        }
    }

    private fun submitForm() {
        produkViewModel.submitForm(
            binding.etProductName.text.toString(),
            binding.etSatuan.text.toString(),
            binding.etNormalPrice.text.toString(),
            binding.etResellerPrice.text.toString(),
        )
    }

    private fun updateUI() {

        produkViewModel.productFormState.observe(viewLifecycleOwner) {
            binding.btnSimpan.isEnabled = it.isDataValid

            binding.tietProductName.error = it.namaError
            binding.tietSatuan.error = it.satuanError
            binding.tietNormalPrice.error = it.hargaNormalError
            binding.tietResellerPrice.error = it.hargaResellerError
        }

        produkViewModel.productList.observe(viewLifecycleOwner) {
            binding.loading.visibility = View.GONE

            if (it is Resource.Success && it.value.message.equals("Data created", true)) {
                Toast.makeText(requireContext(), "Tambah data berhasil", Toast.LENGTH_SHORT).show()

                binding.etProductName.text = null
                binding.etSatuan.text = null
                binding.etNormalPrice.text = null
                binding.etResellerPrice.text = null
                produkViewModel.resetSelectedProduct()
                produkViewModel.resetMessageResponse()
                findNavController().navigate(FormProductFragmentDirections.actionFormProductFragmentToProductFragment())
            } else if (it is Resource.Success && it.value.message.equals("Data updated", true)) {
                Toast.makeText(requireContext(), "Update data berhasil", Toast.LENGTH_SHORT).show()

                binding.etProductName.text = null
                binding.etSatuan.text = null
                binding.etNormalPrice.text = null
                binding.etResellerPrice.text = null

                val oldItem = produkViewModel.selectedProduct.value
                val newItem = it.value.data!!.produk!!.first { item ->
                    item!!.idBarang == oldItem!!.idBarang
                }
                produkViewModel.updateSelectedProduct(newItem!!)
                produkViewModel.resetMessageResponse()
                findNavController().navigate(FormProductFragmentDirections.actionFormProductFragmentToProductFragment())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "FormProductFragment"
    }
}
