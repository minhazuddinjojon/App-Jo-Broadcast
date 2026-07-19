package com.bracu.cse489.assignment2.ui.broadcast

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentCustomBroadcastInputBinding

/**
 * Assignment 2 - Part A, Screen 2/3 (Custom Broadcast Receiver path).
 * Takes a plain text message from the user - validated live, Proceed only enables
 * once there's real content - and passes it forward, via Navigation Safe Args, to
 * [CustomBroadcastReceiverFragment].
 */
class CustomBroadcastInputFragment : Fragment(R.layout.fragment_custom_broadcast_input) {

    private var _binding: FragmentCustomBroadcastInputBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCustomBroadcastInputBinding.bind(view)

        binding.btnProceed.isEnabled = false
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tilMessage.error = null
                binding.btnProceed.isEnabled = !s.isNullOrBlank()
            }
        })

        binding.btnProceed.setOnClickListener {
            val text = binding.etMessage.text?.toString()?.trim().orEmpty()
            if (text.isEmpty()) {
                binding.tilMessage.error = getString(R.string.error_empty_message)
                return@setOnClickListener
            }
            binding.tilMessage.error = null
            findNavController().navigate(
                CustomBroadcastInputFragmentDirections
                    .actionCustomBroadcastInputFragmentToCustomBroadcastReceiverFragment(text)
            )
        }

        playEntranceAnimation()
    }

    private fun playEntranceAnimation() {
        binding.root.alpha = 0f
        binding.root.translationY = 24f
        binding.root.animate().alpha(1f).translationY(0f).setDuration(280).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
