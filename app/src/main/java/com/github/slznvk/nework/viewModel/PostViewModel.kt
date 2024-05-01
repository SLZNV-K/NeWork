package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.repository.PostRepository
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _data = MutableLiveData<List<Post>>()
    val data: LiveData<List<Post>>
        get() = _data

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _pickedPost = MutableLiveData<Post>()
    val pickedPost: LiveData<Post>
        get() = _pickedPost

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.value = StateModel(loading = true)
                _data.postValue(postRepository.getAllPosts())
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun getPostById(id: Int) {
        viewModelScope.launch {
            try {
                _pickedPost.postValue(postRepository.getPostById(id))
                _dataState.value = StateModel(loading = true)
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }

        }
    }

    fun savePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clear() {
        _photo.value = null
    }
}