package com.kencana.titipjual.ui.penjualan.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.ProdukRepository
import com.kencana.titipjual.data.response.ProdukItem
import com.kencana.titipjual.data.response.ProdukResponse
import com.kencana.titipjual.data.response.SalesProductRatingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectBarangViewModel @Inject constructor(
    private val produkRepository: ProdukRepository
) : ViewModel() {

    private var _rating: MutableLiveData<Resource<SalesProductRatingResponse>> = MutableLiveData()
    val rating: LiveData<Resource<SalesProductRatingResponse>> get() = _rating

    private var _productList: MutableLiveData<Resource<ProdukResponse>> = MutableLiveData()
    val productList: LiveData<Resource<ProdukResponse>> get() = _productList

    private var _selectedProduct: MutableLiveData<ProdukItem> = MutableLiveData(ProdukItem())
    val selectedProduct: LiveData<ProdukItem> get() = _selectedProduct

    private var _qty: MutableLiveData<String> = MutableLiveData(String())
    val qty: LiveData<String> get() = _qty

    private var _productFormState: MutableLiveData<SelectBarangFormState> = MutableLiveData(
        SelectBarangFormState()
    )
    val productFormState: LiveData<SelectBarangFormState> get() = _productFormState

    fun getList() = viewModelScope.launch {
        _productList.value = produkRepository.getProduct()
    }

    fun setSelected(produk: ProdukItem) {
        _selectedProduct.value = produk
    }

    fun submitForm(
        qty: String,
    ) {
        _qty.value = qty

        if (selectedProduct.value == ProdukItem()) {
            _productFormState.value = SelectBarangFormState(namaError = "Produk tidak boleh kosong")
        } else if (qty.isEmpty()) {
            _productFormState.value = SelectBarangFormState(qtyError = "Jumlah tidak boleh kosong")
        } else {
            _productFormState.value =
                SelectBarangFormState(isDataValid = true)
        }
    }

    fun reset() {
        _qty.value = "0"
        _selectedProduct.value = ProdukItem()
    }

    fun getRating(product_id: String, seller_id: String) = viewModelScope.launch {
        _rating.value = produkRepository.getRating(product_id, seller_id)
    }

    init {
        getList()
    }
}
