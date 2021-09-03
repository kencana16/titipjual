package com.kencana.titipjual.ui.penjualan.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.PenjualanRepository
import com.kencana.titipjual.data.response.DetailBarangItemPenjualan
import com.kencana.titipjual.data.response.PenjualanItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPenjualanViewModel @Inject constructor(
    private val penjualanRepository: PenjualanRepository
) : ViewModel() {
    private var _penjualan: MutableLiveData<PenjualanItem> = MutableLiveData<PenjualanItem>(null)
    val penjualan: LiveData<PenjualanItem> get() = _penjualan

    fun getData(id: String) = viewModelScope.launch {
        val resources = penjualanRepository.getSales(id)
        Log.d("TAG", "getData: $resources")
        if (resources is Resource.Success) {
            _penjualan.postValue(resources.value.data?.penjualan?.get(0))
            Log.d(TAG, "getData: ${resources.value.data?.penjualan?.get(0)}")
        }
    }

    fun setData(item: PenjualanItem) {
        _penjualan.value = item
    }

    fun updateProduct(item: DetailBarangItemPenjualan) {
        val detailBarangItem = penjualan.value?.detailBarang
        detailBarangItem?.add(item)
        val itemCount = detailBarangItem?.groupBy { it?.idBarang }?.values?.map {
            it.reduce { acc, detailBarangItem ->
                DetailBarangItemPenjualan(
                    idDetailPenjualan = acc?.idDetailPenjualan,
                    idPenjualan = acc?.idPenjualan,
                    idBarang = acc?.idBarang,
                    jmlProduk = acc?.jmlProduk,
                    jmlTerjual = detailBarangItem?.jmlTerjual,
                    jmlUang = detailBarangItem?.jmlTerjual?.toLong()
                        ?.times(acc?.hargaSatuanReseller!!.toLong()).toString(),
                    dateCreated = acc?.dateCreated,
                    dateModified = acc?.dateModified,
                    namaBarang = acc?.namaBarang,
                    hargaSatuanReseller = acc?.hargaSatuanReseller
                )
            }
        }
        var jml: Long = 0
        itemCount?.forEach {
            val i: Long = it?.jmlUang?.toLong() ?: 0
            jml += i
        }

        _penjualan.value =
            penjualan.value?.copy(detailBarang = itemCount?.toMutableList(), total = jml.toString())
    }

    fun saveProduct() = viewModelScope.launch {
        penjualanRepository.updateProduct(penjualan.value!!)
        getData(penjualan.value?.idPenjualan!!)
    }

    fun addPayment(pay: String) = viewModelScope.launch {
        val resources = penjualanRepository.addPayment(penjualan.value?.idPenjualan!!, pay)
        Log.d(TAG, "updateUI: $resources")
        if (resources is Resource.Success) {
            _penjualan.value = resources.value.data?.penjualan?.get(0)
        }
    }

    fun resetData() = viewModelScope.launch {
        val resources = penjualanRepository.getSales(penjualan.value!!.idPenjualan!!)
        if (resources is Resource.Success) {
            _penjualan.value = resources.value.data?.penjualan?.get(0)
        }
    }

    companion object {
        private const val TAG = "DetailPenjualanViewMode"
    }
}
