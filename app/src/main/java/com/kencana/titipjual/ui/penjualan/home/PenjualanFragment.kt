package com.kencana.titipjual.ui.penjualan.home

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
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.data.response.PenjualanItem
import com.kencana.titipjual.databinding.FragmentPenjualanBinding
import com.kencana.titipjual.ui.penjualan.home.adapter.DateAdapter
import com.kencana.titipjual.ui.penjualan.home.adapter.SalesAdapter
import com.kencana.titipjual.ui.penjualan.home.adapter.SellerAdapter
import com.kencana.titipjual.utils.decimalFormat
import com.kencana.titipjual.utils.getDateTime
import com.kencana.titipjual.utils.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PenjualanFragment : Fragment(R.layout.fragment_penjualan) {

    private val penjualanViewModel: PenjualanViewModel by hiltNavGraphViewModels(R.id.nav_penjualan)
    private var _binding: FragmentPenjualanBinding? = null
    private val binding: FragmentPenjualanBinding get() = _binding!!
    private lateinit var sellerAdapter: SellerAdapter
    private lateinit var belumDirekapAdapter: SalesAdapter
    private lateinit var belumDibayarAdapter: SalesAdapter
    private lateinit var selesaiAdapter: SalesAdapter

    private lateinit var sellerSheet: BottomSheetBehavior<LinearLayout>
    private lateinit var dateSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenjualanBinding.inflate(inflater, container, false)
        val root = binding.root

//        TODO("Make expandable layout for recycler")
        setupUI()
        updateUI()

        binding.swipeRefresh.setOnRefreshListener {
            penjualanViewModel.refresh()
        }

        return root
    }

    private fun showSeller() {
        dateSheet.state = BottomSheetBehavior.STATE_HIDDEN
        sellerSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showDate() {
        dateSheet.state = BottomSheetBehavior.STATE_EXPANDED
        sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupUI() {

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            findNavController().navigate(
                PenjualanFragmentDirections.actionPenjualanFragmentToAddPenjualanFragment()
            )
        }

        binding.tvSeller.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                penjualanViewModel.getSeller()
                showSeller()
            }
        }

        binding.tvDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDate()
            }
        }

        binding.btnSearchSales.setOnClickListener {
            penjualanViewModel.getData()
        }

        binding.btnSearchReset.setOnClickListener {
            penjualanViewModel.resetData()
        }

        binding.rvBelumDirekap.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            belumDirekapAdapter = SalesAdapter()
            adapter = belumDirekapAdapter
            belumDirekapAdapter.setOnItemClickListener(object : SalesAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualanItem) {
                    findNavController().navigate(
                        PenjualanFragmentDirections.actionPenjualanFragmentToDetailPenjualanFragment(
                            item
                        )
                    )
                }
            })
        }
        binding.rvBelumDibayar.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            belumDibayarAdapter = SalesAdapter()
            adapter = belumDibayarAdapter
            belumDibayarAdapter.setOnItemClickListener(object : SalesAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualanItem) {
                    findNavController().navigate(
                        PenjualanFragmentDirections.actionPenjualanFragmentToDetailPenjualanFragment(
                            item
                        )
                    )
                }
            })
        }
        binding.rvSelesai.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            selesaiAdapter = SalesAdapter()
            adapter = selesaiAdapter
            selesaiAdapter.setOnItemClickListener(object : SalesAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualanItem) {
                    findNavController().navigate(
                        PenjualanFragmentDirections.actionPenjualanFragmentToDetailPenjualanFragment(
                            item
                        )
                    )
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
                    penjualanViewModel.setSeller(item)
                    sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
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
                            penjualanViewModel.setDate(date ?: "")
                        }
                        DateAdapter.TEXT_KEMARIN -> {
                            val date =
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds() - 86400000)
                            penjualanViewModel.setDate(date ?: "")
                        }
                        DateAdapter.TEXT_MINGGU_INI -> {
                            val date =
                                "${getDateTime(MaterialDatePicker.todayInUtcMilliseconds() - 86400000 * 7)} - ${
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
                                }"
                            penjualanViewModel.setDate(date)
                        }
                        DateAdapter.TEXT_BULAN_INI -> {
                            val date =
                                "${getDateTime(MaterialDatePicker.thisMonthInUtcMilliseconds())} - ${
                                getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
                                }"
                            penjualanViewModel.setDate(date)
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
        penjualanViewModel.income.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is Resource.Success -> {
                        val todayIncome =
                            decimalFormat.format(it.value.data?.thisDayIncome?.toInt())
                        val thisMonthIncome =
                            decimalFormat.format(it.value.data?.thisMonthIncome?.toInt())

                        binding.tvIncomeToday.text = getString(R.string.rp, todayIncome)
                        binding.tvIncomeThisMonth.text =
                            getString(R.string.this_month, thisMonthIncome)
                    }
                    else -> {
                    }
                }

                hideLoading()
            }
        )

        penjualanViewModel.sellerList.observe(
            viewLifecycleOwner,
            {
                sellerAdapter.submitList(it)

                hideLoading()
            }
        )

        penjualanViewModel.seller.observe(
            viewLifecycleOwner,
            {
                binding.tvSeller.setText(it.namaPenjual)

                hideLoading()
            }
        )

        penjualanViewModel.date.observe(
            viewLifecycleOwner,
            {
                binding.tvDate.setText(it)

                hideLoading()
            }
        )

        penjualanViewModel.sales.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val list: List<PenjualanItem?>? = it.value.data?.penjualan
                    val belumDirekap = list?.filter { it?.status == "0" }
                    val belumDibayar = list?.filter { it?.status == "1" }
                    val selesai = list?.filter { it?.status == "2" }

                    belumDirekapAdapter.submitList(belumDirekap)
                    belumDibayarAdapter.submitList(belumDibayar)
                    selesaiAdapter.submitList(selesai)

                    binding.labelTidakAdaPenjualan.visibility =
                        if (it.value.success == false) View.VISIBLE else View.GONE
                    binding.labelBelumDibayar.visibility =
                        if (belumDibayar.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.labelBelumDirekap.visibility =
                        if (belumDirekap.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.labelSelesai.visibility =
                        if (selesai.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.rvBelumDibayar.visibility =
                        if (belumDibayar.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.rvBelumDirekap.visibility =
                        if (belumDirekap.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.rvSelesai.visibility =
                        if (selesai.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                is Resource.Failure ->
                    handleApiError(it) {
                        penjualanViewModel.getData()
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
            penjualanViewModel.setDate(
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
            penjualanViewModel.setDate("${getDateTime(it)}")
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

    companion object {
        const val TAG = "PenjualanFragment"
    }
}
