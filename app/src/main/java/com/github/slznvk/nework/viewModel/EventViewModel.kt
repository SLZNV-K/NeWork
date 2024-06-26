package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.repository.EventRepository
import com.github.slznvk.nework.auth.AppAuth
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    published = "",
    likeOwnerIds = emptyList(),
    participantsIds = emptyList(),
    speakerIds = emptyList(),
)
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val cached = eventRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> = appAuth.authState.flatMapLatest { (myId, _) ->
        cached.map { pagingData ->
            pagingData.map { event ->
                event.copy(ownedByMe = event.authorId == myId)
            }
        }
    }

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _pickedEvent = MutableLiveData<Event>()
    val pickedEvent: LiveData<Event>
        get() = _pickedEvent

    private val edited = MutableLiveData<Event>()
    private val _eventCreated = MutableLiveData<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun getEventById(id: Int) {
        viewModelScope.launch {
            _dataState.value = StateModel(loading = true)
            try {
                _pickedEvent.postValue(eventRepository.getEventById(id))
                _dataState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }

        }
    }

    fun likeEventById(event: Event) = viewModelScope.launch {
        try {
            if (!event.likedByMe) {
                eventRepository.likeById(event.id)
            } else {
                eventRepository.dislikeById(event.id)
            }
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun removeEventById(id: Int) {
        viewModelScope.launch {
            _dataState.value = StateModel(loading = true)
            try {
                eventRepository.removeById(id)
                _dataState.value = StateModel()
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun saveEvent() {
        edited.value?.let {
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    eventRepository.save(it)
                    _dataState.value = StateModel()
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(event: Event) {
        edited.value = event
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

    fun changeTypeEvent(type: String) {
        edited.value?.let { event ->
            edited.value = event.copy(type = type)
        }
    }

    fun changeDateEvent(date: String) {
        edited.value?.let { event ->
            edited.value = event.copy(datetime = date)
        }
    }

    fun clear() {
        _photo.value = null
    }
}