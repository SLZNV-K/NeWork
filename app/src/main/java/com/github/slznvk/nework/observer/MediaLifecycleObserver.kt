package com.github.slznvk.nework.observer

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MediaLifecycleObserver : LifecycleEventObserver {

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private fun play() {
        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
        mediaPlayer?.prepareAsync()
    }

    private fun pause() {
        mediaPlayer?.pause()
    }

    fun playSong(url: String, playing: Boolean) {
        this.apply {
            if (playing) {
                pause()
            } else {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(url)
                play()
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> mediaPlayer?.pause()
            Lifecycle.Event.ON_STOP -> {
                mediaPlayer?.release()
                mediaPlayer = null
            }

            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }
}