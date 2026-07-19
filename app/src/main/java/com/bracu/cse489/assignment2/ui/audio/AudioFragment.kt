package com.bracu.cse489.assignment2.ui.audio

import android.animation.ObjectAnimator
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentAudioBinding
import java.util.Locale

/**
 * Assignment 2 - Part D.
 * Plays one audio track inside the app using [MediaPlayer] directly, with a custom
 * play/pause control, a live-updating seek bar, and a "vinyl disc" that spins while
 * audio is playing and pauses in place otherwise.
 * Swap [SAMPLE_AUDIO_URL] for a local res/raw file or any other URL as needed.
 */
class AudioFragment : Fragment(R.layout.fragment_audio) {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private val handler = Handler(Looper.getMainLooper())

    private var discRotation: ObjectAnimator? = null

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            val player = mediaPlayer
            if (player != null && player.isPlaying && _binding != null) {
                binding.seekBarAudio.progress = player.currentPosition
                binding.tvCurrentTime.text = formatDuration(player.currentPosition)
            }
            handler.postDelayed(this, 500L)
        }
    }

    companion object {
        private const val SAMPLE_AUDIO_URL =
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAudioBinding.bind(view)

        discRotation = ObjectAnimator.ofFloat(binding.discArt, View.ROTATION, 0f, 360f).apply {
            duration = 8000L
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        binding.progressAudio.visibility = View.VISIBLE
        binding.btnPlayPauseAudio.isEnabled = false

        mediaPlayer = MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(SAMPLE_AUDIO_URL)
                setOnPreparedListener { player ->
                    isPrepared = true
                    if (_binding != null) {
                        binding.progressAudio.visibility = View.GONE
                        binding.btnPlayPauseAudio.isEnabled = true
                        binding.seekBarAudio.max = player.duration
                        binding.tvTotalTime.text = formatDuration(player.duration)
                    }
                }
                setOnCompletionListener {
                    if (_binding != null) {
                        binding.btnPlayPauseAudio.setImageResource(R.drawable.ic_play)
                        binding.seekBarAudio.progress = 0
                        binding.tvCurrentTime.text = formatDuration(0)
                    }
                    discRotation?.pause()
                    handler.removeCallbacks(updateSeekBarRunnable)
                }
                setOnErrorListener { _, _, _ ->
                    if (_binding != null) {
                        binding.progressAudio.visibility = View.GONE
                        Toast.makeText(requireContext(), R.string.error_media_playback, Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressAudio.visibility = View.GONE
                    Toast.makeText(requireContext(), R.string.error_media_playback, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnPlayPauseAudio.setOnClickListener {
            val player = mediaPlayer ?: return@setOnClickListener
            if (!isPrepared) return@setOnClickListener
            if (player.isPlaying) {
                player.pause()
                binding.btnPlayPauseAudio.setImageResource(R.drawable.ic_play)
                discRotation?.pause()
                handler.removeCallbacks(updateSeekBarRunnable)
            } else {
                player.start()
                binding.btnPlayPauseAudio.setImageResource(R.drawable.ic_pause)
                discRotation?.let { if (it.isStarted) it.resume() else it.start() }
                handler.post(updateSeekBarRunnable)
            }
        }

        binding.seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCurrentTime.text = formatDuration(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(updateSeekBarRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer?.seekTo(seekBar.progress)
                if (mediaPlayer?.isPlaying == true) {
                    handler.post(updateSeekBarRunnable)
                }
            }
        })
    }

    private fun formatDuration(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _binding?.btnPlayPauseAudio?.setImageResource(R.drawable.ic_play)
                discRotation?.pause()
            }
        }
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(updateSeekBarRunnable)
        discRotation?.cancel()
        discRotation = null
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
        super.onDestroyView()
    }
}
