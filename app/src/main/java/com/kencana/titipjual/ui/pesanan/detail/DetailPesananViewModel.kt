package com.kencana.titipjual.ui.pesanan.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.PesananRepository
import com.kencana.titipjual.data.response.PesananItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPesananViewModel @Inject constructor(
    private val pesananRepository: PesananRepository
) : ViewModel() {
    private var _pesanan: MutableLiveData<PesananItem> = MutableLiveData<PesananItem>(null)
    val pesanan: LiveData<PesananItem> get() = _pesanan

    fun getData(id: String) = viewModelScope.launch {
        var resources = pesananRepository.getOrder(id)
        Log.d("TAG", "getData: $resources")
        if (resources is Resource.Success) {
            _pesanan.postValue(resources.value.data?.pesanan?.get(0))
            Log.d(Companion.TAG, "getData: ${resources.value.data?.pesanan?.get(0)}")
        }
    }

    fun setData(item: PesananItem) {
        _pesanan.value = item
    }

//    fun updateProduct(item: DetailBarangItemPenjualan) {
//        var detailBarangItem = penjualan.value?.detailBarang
//        detailBarangItem?.add(item)
//        val itemCount = detailBarangItem?.groupBy { it?.idBarang }?.values?.map {
//            it.reduce { acc, detailBarangItem ->
//                DetailBarangItemPenjualan(
//                    idDetailPenjualan = acc?.idDetailPenjualan,
//                    idPenjualan = acc?.idPenjualan,
//                    idBarang = acc?.idBarang,
//                    jmlProduk = acc?.jmlProduk,
//                    jmlTerjual = detailBarangItem?.jmlTerjual,
//                    jmlUang = detailBarangItem?.jmlTerjual?.toLong()
//                        ?.times(acc?.hargaSatuanReseller!!.toLong()).toString(),
//                    dateCreated = acc?.dateCreated,
//                    dateModified = acc?.dateModified,
//                    namaBarang = acc?.namaBarang,
//                    hargaSatuanReseller = acc?.hargaSatuanReseller
//                )
//            }
//        }
//        var jml: Long = 0
//        itemCount?.forEach {
//            val i: Long = it?.jmlUang?.toLong() ?: 0
//            jml += i
//        }
//
//        _penjualan.value =
//            penjualan.value?.copy(detailBarang = itemCount?.toMutableList(), total = jml.toString())
//    }
//
//    fun saveProduct() = viewModelScope.launch {
//        penjualanRepository.updateProduct(penjualan.value!!)
//        getData(penjualan.value?.idPenjualan!!)
//    }


    fun addPayment(pay: String) = viewModelScope.launch {
        var resources = pesananRepository.addPayment(pesanan.value?.idPesanan!!, pay)
        if (resources is Resource.Success) {
            _pesanan.value = resources.value.data?.pesanan?.get(0)
        }
    }

    fun resetData() = viewModelScope.launch {
        var resources = pesananRepository.getOrder(pesanan.value!!.idPesanan!!)
        if (resources is Resource.Success) {
            _pesanan.value = resources.value.data?.pesanan?.get(0)
        }
    }

    companion object {
        private const val TAG = "DetailPenjualanViewMode"
    }
}
