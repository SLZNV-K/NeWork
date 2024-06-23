package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.github.slznvk.domain.dto.AdditionalProp
import com.github.slznvk.domain.dto.Coords
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.repository.PostRepository
import com.github.slznvk.nework.auth.AppAuth
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.model.StateModel
import com.github.slznvk.nework.utills.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    published = Instant.now().toString(),
    likeOwnerIds = emptyList(),
    mentionIds = emptyList()
)
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val cached = postRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authState.flatMapLatest { (myId, _) ->
        cached.map { pagingData ->
            pagingData.map { post ->
                post.copy(ownedByMe = post.authorId == myId)
            }
        }
    }

    var userWall: Flow<PagingData<Post>>? = null

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _pickedPost = MutableLiveData<Post>()
    val pickedPost: LiveData<Post>
        get() = _pickedPost

    private val edited = MutableLiveData<Post>()
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun loadUserWall(id: Long) = viewModelScope.launch {
        try {
            userWall = data.map { pagingData -> pagingData.filter { it.authorId == id } }
                .flowOn(Dispatchers.Default)
        } catch (e: Exception) {
            throw e
        }
    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            if (!post.likedByMe) {
                postRepository.likeById(post.id)
            } else {
                postRepository.dislikeById(post.id)
            }
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getPostById(id: Long) {
        viewModelScope.launch {
            try {
                _pickedPost.postValue(postRepository.getPostById(id))
                _dataState.value = StateModel(loading = true)
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }

        }
    }

    fun removePostById(id: Long) {
        viewModelScope.launch {
            _dataState.value = StateModel(loading = true)
            try {
                postRepository.removeById(id)
                _dataState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun addUsers(users: Map<Long, AdditionalProp>) {
        edited.value?.let {
            edited.value = it.copy(users = users)
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

    fun addPoint(lat: Double, long: Double) {
        edited.value?.let {
            edited.value = it.copy(coords = Coords(lat, long))
        }
    }

    fun clear() {
        _photo.value = null
    }
}