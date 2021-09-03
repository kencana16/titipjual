package com.kencana.titipjual.ui.pesanan.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.PesananRepository
import com.kencana.titipjual.data.repository.SellerRepository
import com.kencana.titipjual.data.response.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPesananViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    private val pesananRepository: PesananRepository
) : ViewModel() {

    private var _response: MutableLiveData<Resource<OrderResponse>> = MutableLiveData()
    val response: LiveData<Resource<OrderResponse>> get() = _response

    private var _pesananItem: MutableLiveData<PesananItem> = MutableLiveData(PesananItem())
    val pesananItem: LiveData<PesananItem> get() = _pesananItem

    enum class Harga { harga_satuan_normal, harga_satuan_reseller }

    private var _harga: MutableLiveData<Harga> = MutableLiveData(Harga.harga_satuan_normal)
    val harga: LiveData<Harga> get() = _harga

    private var _sellerList: MutableLiveData<List<PenjualItem>> =
        MutableLiveData<List<PenjualItem>>()
    val sellerList: LiveData<List<PenjualItem>> get() = _sellerList

    private var _seller: MutableLiveData<PenjualItem> =
        MutableLiveData<PenjualItem>()
    val seller: LiveData<PenjualItem> get() = _seller

    private var _pesananFormState: MutableLiveData<PesananFormState> = MutableLiveData(
        PesananFormState()
    )
    val pesananFormState: LiveData<PesananFormState> get() = _pesananFormState

    fun setHarga(harga: Harga) {
        _harga.postValue(harga)

        val list = _pesananItem.value?.detailBarang?.map {
            val harga_satuan = if (harga == Harga.harga_satuan_normal) {
                it?.hargaSatuanNormal?.toLong()
            } else if (harga == Harga.harga_satuan_reseller) {
                it?.hargaSatuanReseller?.toLong()
            } else {
                0
            }
//            it?.hargaSatuan = harga_satuan.toString()
//            it?.subtotal = harga_satuan?.times(it?.jmlBarang!!.toLong()).toString()
            it?.copy(
                hargaSatuan = harga_satuan.toString(),
                subtotal = harga_satuan?.times(it.jmlBarang!!.toLong()).toString()
            )
        }

        _pesananItem.value = pesananItem.value?.copy(
            jenisHarga = harga.toString(),
            detailBarang = list?.toMutableList()
        )
    }

    fun setDate(date: String) {
        _pesananItem.value =
            _pesananItem.value?.copy(tglDiambil = date)
    }

    fun submitForm(
        nama: String,
        phone: String,
        address: String
    ) {
        _pesananItem.value = pesananItem.value?.copy(namaPemesan = nama)
        _pesananItem.value = pesananItem.value?.copy(noHpPemesan = phone)
        _pesananItem.value = pesananItem.value?.copy(alamat = address)

        validate()
    }

    fun validate() {
        if (pesananItem.value!!.tglDiambil.isNullOrEmpty()) {
            _pesananFormState.value = PesananFormState(tglError = "Tanggal tidak boleh kosong")
        } else if (pesananItem.value!!.namaPemesan.isNullOrEmpty()) {
            _pesananFormState.value = PesananFormState(namaError = "Nama tidak boleh kosong")
        } else if (pesananItem.value!!.noHpPemesan.isNullOrEmpty() || pesananItem.value!!.noHpPemesan?.let { it.length < 10 } == true) {
            _pesananFormState.value = PesananFormState(hpError = "No.HP tidak boleh kosong")
        } else if (pesananItem.value!!.jenisHarga.isNullOrEmpty()) {
            _pesananFormState.value =
                PesananFormState(hargaError = "Pilihan harga tidak boleh kosong")
        } else if (pesananItem.value!!.alamat.isNullOrEmpty()) {
            _pesananFormState.value = PesananFormState(alamatEror = "alamat tidak boleh kosong")
        } else if (pesananItem.value!!.detailBarang.isNullOrEmpty()) {
            _pesananFormState.value = PesananFormState(barangError = "Tambahkan barang")
        } else {
            _pesananFormState.value = PesananFormState(isDataValid = true)
        }
    }

    fun addProduct(product: ProdukItem, qty: String) {
        if (_pesananItem.value?.detailBarang == null) {
            _pesananItem.value?.detailBarang = mutableListOf<DetailBarangItemPesanan?>()
        }

        val harga_satuan = if (harga.value == Harga.harga_satuan_normal) {
            product.hargaSatuanNormal?.toLong()
        } else if (harga.value == Harga.harga_satuan_reseller) {
            product.hargaSatuanReseller?.toLong()
        } else {
            0
        }
        val barang = DetailBarangItemPesanan(
            idBarang = product.idBarang,
            namaBarang = product.namaBarang,
            jmlBarang = qty,
            hargaSatuanNormal = product.hargaSatuanNormal,
            hargaSatuanReseller = product.hargaSatuanReseller,
            hargaSatuan = harga_satuan.toString(),
            subtotal = harga_satuan?.times(qty.toLong()).toString(),
        )
        val barangList = _pesananItem.value?.detailBarang?.toMutableList()
        barangList?.add(barang)
        val itemCount = barangList?.groupBy { it?.idBarang }?.values?.map {
            it.reduce { acc, item ->
                DetailBarangItemPesanan(
                    idBarang = item?.idBarang,
                    namaBarang = item?.namaBarang,
                    jmlBarang = (
                        acc?.jmlBarang?.toInt()
                            ?.plus(item?.jmlBarang?.toInt()!!)
                        ).toString(),
                    hargaSatuanNormal = item?.hargaSatuanNormal,
                    hargaSatuanReseller = item?.hargaSatuanReseller,
                    hargaSatuan = item?.hargaSatuan,
                    subtotal = item?.subtotal
                )
            }
        }
        _pesananItem.value = _pesananItem.value?.copy(detailBarang = itemCount?.toMutableList())
    }

    fun removeProduct(product: DetailBarangItemPesanan) {
        val barangList = _pesananItem.value?.detailBarang?.toMutableList()
        barangList?.remove(product)
        _pesananItem.value =
            _pesananItem.value?.copy(detailBarang = barangList?.toMutableList())
    }

    fun getSeller() = viewModelScope.launch {
        val resource = sellerRepository.getSeller()
        if (resource is Resource.Success) {
            _sellerList.value = resource.value.data?.penjual!!
        }
    }

    fun setSeller(sellerId: PenjualItem) {
        _seller.value = sellerId
        _pesananItem.value = _pesananItem.value?.copy(idPenjual = sellerId.idPenjual)
    }

    fun save() = viewModelScope.launch {
        _response.value = pesananRepository.addOrder(pesananItem.value!!)
    }

    fun reset() {
        _response.value = null
        _pesananItem.value = _pesananItem.value?.copy(idPenjual = null)
        _pesananItem.value = PesananItem()
        _seller.value = PenjualItem()
    }

    init {
        _pesananItem.value =
            _pesananItem.value?.copy(jenisHarga = Harga.harga_satuan_normal.toString())
    }

    companion object {
        private const val TAG = "AddPesananViewModel"
    }
}
