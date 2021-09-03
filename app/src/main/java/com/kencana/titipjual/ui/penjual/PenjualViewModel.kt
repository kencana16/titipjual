package com.kencana.titipjual.ui.penjual

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.SellerRepository
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.data.response.SellerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PenjualViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {
    private var _sellerList: MutableLiveData<Resource<SellerResponse>> = MutableLiveData()
    val sellerList: LiveData<Resource<SellerResponse>> get() = _sellerList

    private var _selectedSeller: MutableLiveData<PenjualItem> = MutableLiveData(PenjualItem())
    val selectedSeller: LiveData<PenjualItem> get() = _selectedSeller

    private var _sellerFormState: MutableLiveData<SellerFormState> = MutableLiveData(
        SellerFormState()
    )
    val sellerFormState: LiveData<SellerFormState> get() = _sellerFormState

    fun getList() = viewModelScope.launch {
        _sellerList.value = sellerRepository.getSeller()
    }

    fun submitForm(
        nama: String,
        hp: String,
        alamat: String,
    ) {
        _selectedSeller.value = selectedSeller.value!!.copy(namaPenjual = nama)
        _selectedSeller.value = selectedSeller.value!!.copy(noHp = hp)
        _selectedSeller.value = selectedSeller.value!!.copy(alamat = alamat)

        if (nama.isEmpty()) {
            _sellerFormState.value = SellerFormState(namaError = "Nama tidak boleh kosong")
        } else if (hp.isEmpty()) {
            _sellerFormState.value = SellerFormState(hpError = "Satuan tidak boleh kosong")
        } else {
            _sellerFormState.value =
                SellerFormState(isDataValid = true)
        }
    }

    fun save() = viewModelScope.launch {
        if (_selectedSeller.value?.idPenjual.isNullOrEmpty()) {
            val resource = sellerRepository.addSeller(selectedSeller.value ?: PenjualItem())
            if (resource is Resource.Success && resource != sellerList.value) {
                _sellerList.value = resource
            }
        } else {
            val resource = sellerRepository.editSeller(selectedSeller.value ?: PenjualItem())
            if (resource is Resource.Success && resource != sellerList.value) {
                _sellerList.value = resource
            }
        }
    }

    fun resetSelectedSeller() {
        _selectedSeller.value = PenjualItem()
        _sellerFormState.value = SellerFormState()
    }

    fun updateSelectedProduct(item: PenjualItem) {
        _selectedSeller.value = item
    }

    fun deleteSelected() = viewModelScope.launch {
        val resource = sellerRepository.deleteSeller(selectedSeller.value!!.idPenjual!!)
        Log.d("TAG", "deleteSelected: $resource")
        if (resource is Resource.Success && resource != sellerList.value) {
            _sellerList.value = resource
            _selectedSeller.value = PenjualItem()
        }
    }

    fun resetMessageResponse() {
        val response = (sellerList.value as Resource.Success).value.copy(message = null)
        _sellerList.value = Resource.Success(response)
    }

    init {
        getList()
    }
}
