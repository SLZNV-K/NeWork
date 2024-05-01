package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slznvk.domain.dto.User
import com.github.slznvk.domain.repository.UserRepository
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _data = MutableLiveData<List<User>>()
    val data: LiveData<List<User>>
        get() = _data

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun loadUsers() {
        viewModelScope.launch {
            try {
                _dataState.value = StateModel(loading = true)
                _data.value = userRepository.getAllUsers()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }
}