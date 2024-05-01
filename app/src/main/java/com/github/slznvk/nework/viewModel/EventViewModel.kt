package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.repository.EventRepository
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _data = MutableLiveData<List<Event>>()

    val data: LiveData<List<Event>>
        get() = _data

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun loadEvents() {
        viewModelScope.launch {
            try {
                _dataState.value = StateModel(loading = true)
                _data.postValue(repository.getAllEvents())
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }
}