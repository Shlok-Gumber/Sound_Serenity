package com.example.intro_video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import org.chromium.base.Log

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var videoView: VideoView
    private lateinit var mediaPlayer: MediaPlayer
    private var videoPosition = 0
    private var audioPosition = 0
    private var isPlaying = false

    // Firebase authentication and Google Sign-In client
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Declare the ActivityResultLauncher
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Make sure to set this correctly
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        videoView = findViewById(R.id.video_view)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)

        val googleSignInButton = findViewById<SignInButton>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Initialize the ActivityResultLauncher
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } else {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Get video and audio positions from Intent
        videoPosition = intent.getIntExtra("VIDEO_POSITION", 0)
        audioPosition = intent.getIntExtra("AUDIO_POSITION", 0)
        isPlaying = intent.getBooleanExtra("IS_PLAYING", false)

        // Set up and start video
        val uri = Uri.parse("android.resource://${packageName}/${R.raw.intro_video}")
        videoView.setVideoURI(uri)
        videoView.seekTo(videoPosition) // Seek to the passed video position

        // Start video playback when prepared
        videoView.setOnPreparedListener {
            videoView.start()
        }

        // Set up MediaPlayer for audio
        mediaPlayer = MediaPlayer.create(this, R.raw.intro_audio)
        mediaPlayer.seekTo(audioPosition) // Seek to the audio position

        // Resume playback based on previous state
        if (isPlaying) {
            mediaPlayer.start()
        }

        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show()
            true
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signUp(email, password)
        }
    }

    private fun signUp(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                        // Optionally, navigate to another activity after successful sign-up
                    } else {
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please check your input. Ensure all fields are filled.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent) // Use the ActivityResultLauncher to start the intent
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Successfully signed in
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                    // Navigate to another activity or update UI
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            audioPosition = mediaPlayer.currentPosition
            mediaPlayer.pause() // Pause audio when activity is paused
        }
        videoPosition = videoView.currentPosition
        videoView.pause() // Pause video when activity is paused
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(videoPosition)
        videoView.start() // Resume video playback when activity is resumed
        mediaPlayer.seekTo(audioPosition)
        if (isPlaying) {
            mediaPlayer.start() // Resume audio playback based on the previous state
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop() // Stop audio playback on activity destroy
        }
        mediaPlayer.release()
    }
}