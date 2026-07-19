package com.bracu.cse489.assignment2.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentCustomBroadcastReceiverBinding

/**
 * Assignment 2 - Part A, Screen 3/3 (Custom Broadcast Receiver path).
 * Creates and registers a custom [BroadcastReceiver] for [ACTION_CUSTOM_MESSAGE].
 * Tapping "Send Broadcast" broadcasts the text message collected on the previous
 * screen (received here via Navigation Safe Args); the registered receiver then
 * catches it in onReceive() and the received text is displayed with a small pulse
 * animation for clear, immediate feedback that it was really delivered live.
 */
class CustomBroadcastReceiverFragment : Fragment(R.layout.fragment_custom_broadcast_receiver) {

    companion object {
        const val ACTION_CUSTOM_MESSAGE = "com.bracu.cse489.assignment2.ACTION_CUSTOM_MESSAGE"
        const val EXTRA_MESSAGE = "extra_message"
    }

    private var _binding: FragmentCustomBroadcastReceiverBinding? = null
    private val binding get() = _binding!!

    private val args: CustomBroadcastReceiverFragmentArgs by navArgs()

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (_binding != null) {
                val message = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
                binding.tvReceivedMessage.text = getString(R.string.received_message_format, message)
                pulseReceivedCard()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCustomBroadcastReceiverBinding.bind(view)

        binding.tvSentMessage.text =
            getString(R.string.message_from_previous_screen_format, args.message)

        binding.btnSendBroadcast.setOnClickListener {
            val intent = Intent(ACTION_CUSTOM_MESSAGE).apply {
                putExtra(EXTRA_MESSAGE, args.message)
                setPackage(requireContext().packageName)
            }
            requireContext().sendBroadcast(intent)
        }

        binding.root.alpha = 0f
        binding.root.translationY = 24f
        binding.root.animate().alpha(1f).translationY(0f).setDuration(280).start()
    }

    private fun pulseReceivedCard() {
        binding.cardReceived.animate()
            .scaleX(1.04f).scaleY(1.04f)
            .setDuration(120)
            .withEndAction {
                _binding?.cardReceived?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(160)?.start()
            }
            .start()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ACTION_CUSTOM_MESSAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(customReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(customReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(customReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
