package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.User
import com.github.slznvk.domain.repository.UserRepository
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = ""
)

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

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>>
        get() = _jobs

    private val _jobsState = MutableLiveData<StateModel>()
    val jobsState: LiveData<StateModel>
        get() = _jobsState

    private val _pickedUser = MutableLiveData<User>()
    val pickedUser: LiveData<User>
        get() = _pickedUser

    private val _editedJob = MutableLiveData<Job>()
    private val _jobCreated = MutableLiveData<Unit>()

    fun loadUsers() {
        viewModelScope.launch {
            _dataState.value = StateModel(loading = true)
            try {
                _data.value = userRepository.getAllUsers()
                _dataState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            try {
                _pickedUser.postValue(userRepository.getUserById(id))
                _dataState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun getUserJobs(userId: Int) {
        _jobsState.value = StateModel(loading = true)
        viewModelScope.launch {
            try {
                _jobs.postValue(userRepository.getUserJobs(userId))
                _jobsState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun deleteJodById(id: Int) {
        viewModelScope.launch {
            _jobsState.value = StateModel(loading = true)
            try {
                userRepository.deleteJodById(id)
                _jobsState.value = StateModel()
            } catch (e: Exception) {
                _jobsState.value = StateModel(error = true)
            }
        }
    }

    fun edit(job: Job) {
        _editedJob.value = job
    }

    fun saveJob() {
        _editedJob.value?.let {
            _jobCreated.value = Unit
            viewModelScope.launch {
                try {
                    userRepository.saveJob(it)
                    _jobsState.value = StateModel()
                } catch (e: Exception) {
                    _jobsState.value = StateModel(error = true)
                }
            }
        }
        _editedJob.value = empty
    }

    fun changeContent(
        name: String, position: String, start: String, finish: String, link: String
    ) {
        _editedJob.value?.let { job ->
            _editedJob.value =
                job.copy(
                    name = name,
                    position = position,
                    start = start,
                    finish = finish,
                    link = link
                )
        }
    }
}