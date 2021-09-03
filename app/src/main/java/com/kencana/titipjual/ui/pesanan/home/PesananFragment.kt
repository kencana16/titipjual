package com.kencana.titipjual.ui.pesanan.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.response.PesananItem
import com.kencana.titipjual.databinding.FragmentPesananBinding
import com.kencana.titipjual.ui.penjualan.home.adapter.DateAdapter
import com.kencana.titipjual.utils.getDateTime
import com.kencana.titipjual.utils.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PesananFragment : Fragment() {

    private lateinit var pesananAdapter: PesananAdapter
    private val pesananViewModel: PesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private var _binding: FragmentPesananBinding? = null
    private val binding: FragmentPesananBinding get() = _binding!!

    private lateinit var dateSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesananBinding.inflate(inflater, container, false)
        val root = binding.root

        setupUI()
        updateUI()

        return root
    }

    private fun showDate() {
        dateSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupUI() {

        binding.swipeRefresh.setOnRefreshListener {
            pesananViewModel.refresh()
        }

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            findNavController().navigate(PesananFragmentDirections.actionPesananFragmentToAddPesananFragment())
        }

        binding.tvDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDate()
            }
        }

        binding.btnSearchOrder.setOnClickListener {
            pesananViewModel.getData()
        }

        binding.rvPesanan.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            pesananAdapter = PesananAdapter()
            adapter = pesananAdapter
            pesananAdapter.setOnItemClickListener(object : PesananAdapter.OnItemClickCallback {
                override fun onClick(item: PesananItem) {
                    findNavController().navigate(
                        PesananFragmentDirections.actionPesananFragmentToDetailPesananFragment(
                            item
                        )
                    )
                }
            })
        }

        dateSheet = BottomSheetBehavior.from(binding.bottomSheetTanggal)
        dateSheet.state = BottomSheetBehavior.STATE_HIDDEN
        binding.rvBottomSheetDate.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            val dateAdapter = DateAdapter()
            this.adapter = dateAdapter
            dateAdapter.setOnItemClickListener(object : DateAdapter.OnItemClickCallback {
                override fun onClick(item: String) {
                    when (item) {
                        DateAdapter.TEXT_HARI_INI -> {
                            val date =
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
                            pesananViewModel.setDate(date ?: "")
                        }
                        DateAdapter.TEXT_KEMARIN -> {
                            val date =
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds() - 86400000)
                            pesananViewModel.setDate(date ?: "")
                        }
                        DateAdapter.TEXT_MINGGU_INI -> {
                            val date =
                                "${getDateTime(MaterialDatePicker.todayInUtcMilliseconds() - 86400000 * 7)} - ${
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
                                }"
                            pesananViewModel.setDate(date)
                        }
                        DateAdapter.TEXT_BULAN_INI -> {
                            val date =
                                "${getDateTime(MaterialDatePicker.thisMonthInUtcMilliseconds())} - ${
                                getDateTime(MaterialDatePicker.thisMonthInUtcMilliseconds() + 86400000.times(30.toLong()))
                                }"
                            pesananViewModel.setDate(date)
                        }
                        DateAdapter.TEXT_PILIH_TANGGAL -> {
                            datePicker()
                        }
                        DateAdapter.TEXT_PILIH_RENTANG -> {
                            rangeDatePicker()
                        }
                    }
                    dateSheet.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })
        }
    }

    private fun updateUI() {

        pesananViewModel.date.observe(
            viewLifecycleOwner,
            {
                binding.tvDate.setText(it)
                hideLoading()
            }
        )

        pesananViewModel.order.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    pesananAdapter.submitList(it.value.data?.pesanan)

                    binding.labelTidakAdaPesanan.visibility =
                        if (it.value.success == false) View.VISIBLE else View.GONE
                    binding.labelDaftarPesanan.visibility =
                        if (it.value.data?.pesanan.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.rvPesanan.visibility =
                        if (it.value.data?.pesanan.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                is Resource.Failure ->
                    handleApiError(it) {
                        pesananViewModel.getData()
                    }
                else -> {
                }
            }

            hideLoading()
        }
    }

    fun rangeDatePicker() {
        // Date Picker
        val builder =
            MaterialDatePicker.Builder.dateRangePicker().setTitleText("Pilih rentang tanggal")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            Log.d(
                "DatePicker Activity",
                "Date String = ${picker.headerText}::  Date epoch values::${it.first}:: to :: ${it.second}"
            )
            pesananViewModel.setDate(
                "${getDateTime(it.first)} - ${
                getDateTime(
                    it.second
                )
                }"
            )
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
            pesananViewModel.setDate("${getDateTime(it)}")
        }
    }

    private fun hideLoading() {
        if (binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
