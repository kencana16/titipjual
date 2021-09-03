package com.kencana.titipjual.ui.pesanan.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.PesananRepository
import com.kencana.titipjual.data.response.OrderResponse
import com.kencana.titipjual.utils.getDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PesananViewModel @Inject constructor(
    private val pesananRepository: PesananRepository,
) : ViewModel() {

    private var _order: MutableLiveData<Resource<OrderResponse>> =
        MutableLiveData<Resource<OrderResponse>>()
    val order: LiveData<Resource<OrderResponse>> get() = _order

    private var _date: MutableLiveData<String> =
        MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    fun setDate(str: String) {
        _date.value = str
    }

    init {
        refresh()

        val date =
            "${getDateTime(MaterialDatePicker.todayInUtcMilliseconds())} - ${
            getDateTime(MaterialDatePicker.todayInUtcMilliseconds() - 3600000 + 86400000 * 8)
            }"
        _date.value = date
        getData()
    }

    fun refresh() {

        var time = if (date.value.isNullOrEmpty()) {
            getDateTime(MaterialDatePicker.todayInUtcMilliseconds())
        } else {
            date.value
        }
        setDate(time ?: "")
        getData()
    }

    fun getData() = viewModelScope.launch {
        _order.value =
            pesananRepository.getOrder(
                date = date.value ?: ""
            )
    }
}
