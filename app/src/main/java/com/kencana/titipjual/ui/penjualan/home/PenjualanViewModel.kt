package com.kencana.titipjual.ui.penjualan.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.DashboardRepository
import com.kencana.titipjual.data.repository.PenjualanRepository
import com.kencana.titipjual.data.repository.SellerRepository
import com.kencana.titipjual.data.response.DashboardResponse
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.data.response.SalesResponse
import com.kencana.titipjual.utils.getDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PenjualanViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val sellerRepository: SellerRepository,
    private val penjualanRepository: PenjualanRepository,
) : ViewModel() {

    private var _sales: MutableLiveData<Resource<SalesResponse>> =
        MutableLiveData<Resource<SalesResponse>>()
    val sales: LiveData<Resource<SalesResponse>> get() = _sales

    private var _income: MutableLiveData<Resource<DashboardResponse>> =
        MutableLiveData<Resource<DashboardResponse>>()
    val income: LiveData<Resource<DashboardResponse>> get() = _income

    private var _sellerList: MutableLiveData<List<PenjualItem>> =
        MutableLiveData<List<PenjualItem>>()
    val sellerList: LiveData<List<PenjualItem>> get() = _sellerList

    private var _seller: MutableLiveData<PenjualItem> =
        MutableLiveData<PenjualItem>()
    val seller: LiveData<PenjualItem> get() = _seller

    private var _date: MutableLiveData<String> =
        MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getIncome() = viewModelScope.launch {
        _income.value = dashboardRepository.getIncome()
    }

    fun getSeller() = viewModelScope.launch {
        val resource = sellerRepository.getSeller()
        if (resource is Resource.Success) {
            _sellerList.value = resource.value.data?.penjual!!
        }
    }

    fun getData() = viewModelScope.launch {
        _sales.value =
            penjualanRepository.getSales(
                seller = seller.value?.idPenjual ?: "",
                date = date.value ?: ""
            )
    }

    fun setSeller(sellerId: PenjualItem) {
        _seller.value = sellerId
    }

    fun setDate(str: String) {
        _date.value = str
    }

    fun resetData() = viewModelScope.launch {
        _seller.value = PenjualItem()
        _date.value = getDateTime(MaterialDatePicker.todayInUtcMilliseconds()) ?: ""
        getData()
    }

    init {
        refresh()
    }

    fun refresh() {
        getSeller()
        getIncome()
        _seller.value = PenjualItem()

        var time = if (date.value.isNullOrEmpty()) {
            getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
        } else {
            date.value
        }
        setDate(time ?: "")
        getData()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG", "onCleared: viewmodel cleared")
    }
}
