package com.github.slznvk.nework.auth

import android.content.Context
import com.github.slznvk.data.ApiService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState: MutableStateFlow<AuthState>

    val authState: StateFlow<AuthState>
        get() = _authState

    init {
        val id = prefs.getLong(KEY_ID, 0)
        val token = prefs.getString(KEY_TOKEN, null)

        if (id == 0L || token == null) {
            _authState = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authState = MutableStateFlow(AuthState(id, token))
        }
//        sendPushToken()
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KEY_TOKEN, token)
            commit()
        }
//        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
//        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

//    fun sendPushToken(token: String? = null) {
//        CoroutineScope(Dispatchers.Default).launch {
//            try {
//                val tokenDto = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
//                val entryPoint =
//                    EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
//                entryPoint.getApiService().sendPushToken(tokenDto)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
    }

}

data class AuthState(val id: Long = 0L, val token: String? = null)
