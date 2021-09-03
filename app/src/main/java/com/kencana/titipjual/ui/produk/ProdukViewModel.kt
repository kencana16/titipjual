package com.kencana.titipjual.ui.produk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.ProdukRepository
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.data.response.ProdukResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProdukViewModel @Inject constructor(
    private val produkRepository: ProdukRepository
) : ViewModel() {

    private var _productList: MutableLiveData<Resource<ProdukResponse>> = MutableLiveData()
    val productList: LiveData<Resource<ProdukResponse>> get() = _productList

    private var _selectedProduct: MutableLiveData<ProdukItem> = MutableLiveData(ProdukItem())
    val selectedProduct: LiveData<ProdukItem> get() = _selectedProduct

    private var _productFormState: MutableLiveData<ProductFormState> = MutableLiveData(
        ProductFormState()
    )
    val productFormState: LiveData<ProductFormState> get() = _productFormState

    fun getList() = viewModelScope.launch {
        _productList.value = produkRepository.getProduct()
    }

    fun submitForm(
        nama: String,
        satuan: String,
        hargaNormal: String,
        hargaReseller: String
    ) {
        _selectedProduct.value = selectedProduct.value!!.copy(namaBarang = nama)
        _selectedProduct.value = selectedProduct.value!!.copy(satuan = satuan)
        _selectedProduct.value = selectedProduct.value!!.copy(hargaSatuanNormal = hargaNormal)
        _selectedProduct.value = selectedProduct.value!!.copy(hargaSatuanReseller = hargaReseller)

        if (nama.isEmpty()) {
            _productFormState.value = ProductFormState(namaError = "Nama tidak boleh kosong")
        } else if (satuan.isEmpty()) {
            _productFormState.value = ProductFormState(satuanError = "Satuan tidak boleh kosong")
        } else if (hargaNormal.isEmpty()) {
            _productFormState.value =
                ProductFormState(hargaNormalError = "Harga Normal tidak boleh kosong")
        } else if (hargaReseller.isEmpty()) {
            _productFormState.value =
                ProductFormState(hargaResellerError = "Harga Reseller tidak boleh kosong")
        } else if (hargaReseller.toLong() > hargaNormal.toLong()) {
            _productFormState.value =
                ProductFormState(hargaResellerError = "Harga Reseller tidak boleh lebih mahal dari harga normal")
        } else {
            _productFormState.value =
                ProductFormState(isDataValid = true)
        }
    }

    fun save() = viewModelScope.launch {
        if (_selectedProduct.value?.idBarang.isNullOrEmpty()) {
            val resource = produkRepository.addProduct(selectedProduct.value ?: ProdukItem())
            if (resource is Resource.Success && resource != productList.value) {
                _productList.value = resource
            }
        } else {
            val resource = produkRepository.editProduct(selectedProduct.value ?: ProdukItem())
            if (resource is Resource.Success && resource != productList.value) {
                _productList.value = resource
            }
        }
    }

    fun resetSelectedProduct() {
        _selectedProduct.value = ProdukItem()
        _productFormState.value = ProductFormState()
    }

    fun updateSelectedProduct(item: ProdukItem) {
        _selectedProduct.value = item
    }

    fun deleteSelected() = viewModelScope.launch {
        val resource = produkRepository.deleteProduct(selectedProduct.value!!.idBarang!!)
        Log.d("TAG", "deleteSelected: $resource")
        if (resource is Resource.Success && resource != productList.value) {
            _productList.value = resource
            _selectedProduct.value = ProdukItem()
        }
    }

    fun resetMessageResponse() {
        val response = (productList.value as Resource.Success).value.copy(message = null)
        _productList.value = Resource.Success(response)
    }

    init {
        Log.d(TAG, "Init ")
        getList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(Companion.TAG, "onCleared: ${Companion.TAG} cleared")
    }

    companion object {
        private const val TAG = "ProdukViewModel"
    }
}
