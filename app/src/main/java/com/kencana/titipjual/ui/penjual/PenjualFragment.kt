package com.kencana.titipjual.ui.penjual

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
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.databinding.MasterListBinding
import com.kencana.titipjual.utils.TYPE
import com.kencana.titipjual.utils.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PenjualFragment : Fragment() {

    private val penjualViewModel: PenjualViewModel by hiltNavGraphViewModels(R.id.nav_penjual)
    private var _binding: MasterListBinding? = null
    private val binding: MasterListBinding get() = _binding!!
    private lateinit var penjualAdapter: PenjualAdapter

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
                PenjualFragmentDirections.actionPenjualFragmentToFormPenjualFragment(
                    TYPE.add
                )
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            penjualViewModel.getList()
        }

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            penjualAdapter = PenjualAdapter()
            adapter = penjualAdapter
            penjualAdapter.setOnItemClickCallback(object : PenjualAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualItem) {
                    penjualViewModel.updateSelectedProduct(item)
                    findNavController().navigate(PenjualFragmentDirections.actionPenjualFragmentToDetailPenjualFragment())
                }
            })
        }

        binding.searchView.queryHint = "Cari Penjual"
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(s: String?) {
        val resource = penjualViewModel.sellerList.value
        if (resource is Resource.Success) {
            val list = resource.value.data?.penjual
            val filteredList =
                list?.filter { it.namaPenjual!!.contains(s.toString(), true) }
            penjualAdapter.submitList(filteredList)
            checkEmpty(filteredList)
        }
    }

    private fun updateUI() {
        penjualViewModel.sellerList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val list = it.value.data?.penjual
                    penjualAdapter.submitList(list)
                    checkEmpty(list)
                    hideRefresh()
                }
                is Resource.Failure -> handleApiError(it) { penjualViewModel.getList() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkEmpty(list: List<PenjualItem?>?) {
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
        private const val TAG = "PenjualFragment"
    }
}
