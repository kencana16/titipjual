package com.kencana.titipjual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.repository.UserRepository
import com.kencana.titipjual.ui.base.BaseViewModel
import com.kencana.titipjual.ui.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository
) : BaseViewModel(repository) {

    private var _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    fun getUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            if (response is Resource.Success) {
                _user.value = response.value.data!!.user
            }
        }
    }
}
