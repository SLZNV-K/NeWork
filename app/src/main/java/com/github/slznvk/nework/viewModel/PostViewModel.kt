package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.repository.PostRepository
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    published = "",
    likeOwnerIds = emptyList(),
    mentionIds = emptyList()
)
private val noPhoto = PhotoModel()

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val cached = postRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = cached.flowOn(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _pickedPost = MutableLiveData<Post>()
    val pickedPost: LiveData<Post>
        get() = _pickedPost

    private val edited = MutableLiveData<Post>()
    private val _postCreated = MutableLiveData<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun likeById(post: Post) = viewModelScope.launch {
        _dataState.value = StateModel(loading = true)
        try {
            if (!post.likedByMe) {
                postRepository.likeById(post.id)
            } else {
                postRepository.dislikeById(post.id)
            }
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
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

    fun savePost() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    postRepository.save(it)
                    _dataState.value = StateModel()
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun savePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun changeContent(content: String) {
        edited.value?.let { post ->
            val text = content.trim()
            if (text != post.content) {
                edited.value = post.copy(content = text)
            }
        }
    }

    fun clear() {
        _photo.value = null
    }
}