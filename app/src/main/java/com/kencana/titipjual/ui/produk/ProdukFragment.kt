package com.kencana.titipjual.ui.produk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.databinding.MasterListBinding
import com.kencana.titipjual.utils.TYPE
import com.kencana.titipjual.utils.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProdukFragment : Fragment() {
    private var _binding: MasterListBinding? = null
    private val binding: MasterListBinding get() = _binding!!

    private val produkViewModel: ProdukViewModel by hiltNavGraphViewModels(R.id.nav_product)
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MasterListBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()

        return binding.root
    }

    private fun setupUI() {
        binding.fab.setOnClickListener {
            findNavController().navigate(
                ProdukFragmentDirections.actionProductFragmentToFormProductFragment(
                    TYPE.add
                )
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            produkViewModel.getList()
        }

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            productAdapter = ProductAdapter()
            adapter = productAdapter
            productAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
                override fun onClick(item: ProdukItem) {
                    produkViewModel.updateSelectedProduct(item)
                    findNavController().navigate(ProdukFragmentDirections.actionProductFragmentToDetailProductFragment())
                }
            })
        }

        binding.searchView.queryHint = "Cari Produk"
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(s: String?) {
        val resource = produkViewModel.productList.value
        if (resource is Resource.Success) {
            val list = resource.value.data?.produk
            val filteredList =
                list?.filter { it!!.namaBarang!!.contains(s.toString(), true) }
            productAdapter.submitList(filteredList)
            checkEmpty(filteredList)
        }
    }

    private fun updateUI() {
        produkViewModel.productList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val list = it.value.data?.produk
                    productAdapter.submitList(list)
                    checkEmpty(list)
                    hideRefresh()
                }
                is Resource.Failure -> handleApiError(it) { produkViewModel.getList() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkEmpty(list: List<ProdukItem?>?) {
        binding.recyclerview.visibility =
            if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.labelNotFound.visibility =
            if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    private fun hideRefresh() {
        if (binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    companion object {
        private const val TAG = "ProdukFragment"
    }
}
