package com.github.slznvk.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slznvk.data.api.ApiService
import com.github.slznvk.nework.auth.AppAuth
import com.github.slznvk.nework.error.ApiError
import com.github.slznvk.nework.model.AuthModelState
import com.github.slznvk.nework.model.PhotoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {

    val data = appAuth.authState
    val authenticated: Boolean
        get() = data.value.id != 0

    private val _authStateModel = MutableLiveData<AuthModelState>()
    val authStateModel: LiveData<AuthModelState>
        get() = _authStateModel

    fun authentication(login: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = apiService.authentication(login, pass)

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())

                appAuth.setAuth(body.id, body.token!!)
                _authStateModel.value = AuthModelState()

            } catch (e: Exception) {
                _authStateModel.value = AuthModelState(error = true)
            }
        }
    }

    fun registration(login: String, pass: String, name: String, photo: PhotoModel?) {
        viewModelScope.launch {
            try {
                val media = if (photo != null) {
                    MultipartBody.Part.createFormData(
                        "file",
                        photo.file!!.name,
                        photo.file.asRequestBody()
                    )
                } else null

                val response =
                    apiService.registration(
                        login.toRequestBody("text/plain".toMediaType()),
                        pass.toRequestBody("text/plain".toMediaType()),
                        name.toRequestBody("text/plain".toMediaType()),
                        media
                    )

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body =
                    response.body() ?: throw ApiError(response.code(), response.message())

                appAuth.setAuth(body.id, body.token!!)
                _authStateModel.value = AuthModelState()

            } catch (e: Exception) {
                _authStateModel.value = AuthModelState(error = true)
            }
        }
    }
}