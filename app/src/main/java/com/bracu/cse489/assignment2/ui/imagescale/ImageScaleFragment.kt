package com.bracu.cse489.assignment2.ui.imagescale

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentImageScaleBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Assignment 2 - Part B.
 * Loads an image from the internet with Picasso, then lets the user pinch to zoom
 * and drag to pan it via [PinchZoomImageView]. A live zoom-percentage badge and a
 * "reset zoom" FAB appear as soon as the user zooms in. Swap [SAMPLE_IMAGE_URL] for
 * any other image URL as needed.
 */
class ImageScaleFragment : Fragment(R.layout.fragment_image_scale) {

    private var _binding: FragmentImageScaleBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SAMPLE_IMAGE_URL = "https://picsum.photos/id/237/800/600"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentImageScaleBinding.bind(view)

        binding.progressImage.visibility = View.VISIBLE
        Picasso.get()
            .load(SAMPLE_IMAGE_URL)
            .into(binding.pinchZoomImageView, object : Callback {
                override fun onSuccess() {
                    _binding?.progressImage?.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    if (_binding != null) {
                        binding.progressImage.visibility = View.GONE
                        Toast.makeText(requireContext(), R.string.error_media_playback, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        binding.pinchZoomImageView.onScaleChanged = { scale ->
            if (_binding != null) {
                if (scale > 1.02f) {
                    val percent = (scale * 100).toInt()
                    binding.tvZoomBadge.text = getString(R.string.zoom_percent_format, percent)
                    binding.tvZoomBadge.visibility = View.VISIBLE
                    binding.btnResetZoom.visibility = View.VISIBLE
                } else {
                    binding.tvZoomBadge.visibility = View.GONE
                    binding.btnResetZoom.visibility = View.GONE
                }
            }
        }

        binding.btnResetZoom.setOnClickListener {
            binding.pinchZoomImageView.resetZoom()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
