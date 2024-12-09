package com.example.intro_video

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var audioPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.video_view)
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        val nextButton = findViewById<Button>(R.id.next_button)

        // Set up VideoView with video URI
        val videoUri = Uri.parse("android.resource://${packageName}/raw/intro_video")
        videoView.setVideoURI(videoUri)

        // Prepare and start the video playback
        videoView.start()

        // Create and start MediaPlayer for audio
        mediaPlayer = MediaPlayer.create(this, R.raw.intro_audio)
        mediaPlayer.start()
        isPlaying = true

        // Play video completion listener
        videoView.setOnCompletionListener {
            mediaPlayer.stop()
            mediaPlayer.release()
            isPlaying = false
        }

        // Animation for welcome text
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        welcomeText.startAnimation(fadeInAnimation)

        nextButton.setOnClickListener {
            // Get the current video and audio positions
            val videoPosition = videoView.currentPosition
            audioPosition = mediaPlayer.currentPosition

            // Intent to start SignUpActivity and pass the video and audio positions
            val intent = Intent(this, SignUpActivity::class.java).apply {
                putExtra("VIDEO_POSITION", videoPosition)
                putExtra("AUDIO_POSITION", audioPosition)
                putExtra("IS_PLAYING", isPlaying)
            }
            startActivity(intent)

            // Stop video and audio playback when navigating away
            videoView.stopPlayback()
            mediaPlayer.release()
            isPlaying = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
