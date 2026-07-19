package com.bracu.cse489.assignment2.ui.video

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentVideoBinding

/**
 * Assignment 2 - Part C.
 * Plays one video inside the app using [android.widget.VideoView] (which wraps
 * MediaPlayer) with a [MediaController] overlay for play/pause/seek, plus a custom
 * "Replay" overlay that appears when playback finishes.
 * Swap [SAMPLE_VIDEO_URL] for a local res/raw file or any other URL as needed.
 */
class VideoFragment : Fragment(R.layout.fragment_video) {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SAMPLE_VIDEO_URL =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoBinding.bind(view)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        
        // Use local raw resource for reliability
        val videoPath = "android.resource://" + requireContext().packageName + "/" + R.raw.sample_video
        binding.videoView.setVideoURI(Uri.parse(videoPath))

        binding.progressVideo.visibility = View.VISIBLE

        binding.videoView.setOnPreparedListener { mp ->
            _binding?.progressVideo?.visibility = View.GONE
            mp.isLooping = false
            binding.videoView.start()
        }

        binding.videoView.setOnCompletionListener {
            _binding?.replayOverlay?.visibility = View.VISIBLE
        }

        binding.videoView.setOnErrorListener { _, _, _ ->
            if (_binding != null) {
                binding.progressVideo.visibility = View.GONE
                Toast.makeText(requireContext(), R.string.error_media_playback, Toast.LENGTH_SHORT).show()
            }
            true
        }

        binding.replayOverlay.setOnClickListener {
            binding.replayOverlay.visibility = View.GONE
            binding.videoView.start()
        }
    }

    override fun onPause() {
        super.onPause()
        _binding?.videoView?.let {
            if (it.isPlaying) it.pause()
        }
    }

    override fun onDestroyView() {
        _binding?.videoView?.stopPlayback()
        _binding = null
        super.onDestroyView()
    }
}
