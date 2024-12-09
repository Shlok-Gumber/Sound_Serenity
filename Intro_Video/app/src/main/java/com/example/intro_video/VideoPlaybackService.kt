// VideoPlaybackService.kt
package com.example.intro_video

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class VideoPlaybackService : Service() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource("android.resource://$packageName/${R.raw.intro_video}") // Replace with your video file
        mediaPlayer.setOnPreparedListener { mediaPlayer.start() }
        mediaPlayer.prepareAsync()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start() // Start playback if not already playing
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }
}
