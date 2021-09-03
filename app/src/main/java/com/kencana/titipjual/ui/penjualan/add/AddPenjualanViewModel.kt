package com.kencana.titipjual.ui.penjualan.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.PenjualanRepository
import com.kencana.titipjual.data.response.*
import com.kencana.titipjual.utils.defaultDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPenjualanViewModel @Inject constructor(
    private val penjualanRepository: PenjualanRepository,
) : ViewModel() {

    private var _response: MutableLiveData<Resource<SalesResponse>> = MutableLiveData()
    val response: LiveData<Resource<SalesResponse>> get() = _response

    private var _penjualanItem: MutableLiveData<PenjualanItem> = MutableLiveData(PenjualanItem())
    val penjualanItem: LiveData<PenjualanItem> get() = _penjualanItem

    private var _seller: MutableLiveData<PenjualItem> =
        MutableLiveData<PenjualItem>()
    val seller: LiveData<PenjualItem> get() = _seller

    fun setSeller(sellerId: PenjualItem) {
        _seller.value = sellerId
        _penjualanItem.value = _penjualanItem.value?.copy(idPenjual = sellerId.idPenjual)
    }

    fun setDate(date: String) {
        _penjualanItem.value =
            _penjualanItem.value?.copy(tglPenjualan = defaultDateTime(date.toLong()))
    }

    fun addProduct(product: ProdukItem, qty: String) {
        if (_penjualanItem.value?.detailBarang == null) {
            _penjualanItem.value?.detailBarang = mutableListOf<DetailBarangItemPenjualan?>()
        }

        val barang = DetailBarangItemPenjualan(
            idBarang = product.idBarang,
            namaBarang = product.namaBarang,
            jmlProduk = qty
        )
        val barangList = _penjualanItem.value?.detailBarang?.toMutableList()
        barangList?.add(barang)
        val itemCount = barangList?.groupBy { it?.idBarang }?.values?.map {
            it.reduce { acc, item ->
                DetailBarangItemPenjualan(
                    idBarang = item?.idBarang,
                    namaBarang = item?.namaBarang,
                    jmlProduk = (
                        acc?.jmlProduk?.toInt()
                            ?.plus(item?.jmlProduk?.toInt()!!)
                        ).toString()
                )
            }
        }
        _penjualanItem.value = _penjualanItem.value?.copy(detailBarang = itemCount?.toMutableList())
    }

    fun removeProduct(product: DetailBarangItemPenjualan) {
        val barangList = _penjualanItem.value?.detailBarang?.toMutableList()
        barangList?.remove(product)
        _penjualanItem.value =
            _penjualanItem.value?.copy(detailBarang = barangList?.toMutableList())
    }

    fun save() = viewModelScope.launch {
        _response.value = penjualanRepository.addSales(penjualanItem.value!!)
    }

    fun reset() {
        _response.value = null
        _seller.value = PenjualItem()
        _penjualanItem.value = PenjualanItem()
    }

    companion object {
        private const val TAG = "AddPenjualanViewModel"
    }
}
