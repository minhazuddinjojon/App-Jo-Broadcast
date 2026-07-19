package com.bracu.cse489.assignment2.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentBatteryBroadcastBinding

/**
 * Assignment 2 - Part A, Screen 2/3 (System Battery Notification path).
 * Dynamically registers a [BroadcastReceiver] for [Intent.ACTION_BATTERY_CHANGED]
 * (a sticky system broadcast that must be registered at runtime, not in the manifest)
 * and drives an animated [BatteryRingView] with the live battery percentage.
 * Per the assignment, this path ends here - there is no third screen.
 */
class BatteryBroadcastFragment : Fragment(R.layout.fragment_battery_broadcast) {

    private var _binding: FragmentBatteryBroadcastBinding? = null
    private val binding get() = _binding!!

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0 && _binding != null) {
                val batteryPct = (level * 100 / scale.toFloat()).toInt()
                binding.batteryRingView.setPercentage(batteryPct)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBatteryBroadcastBinding.bind(view)

        binding.root.alpha = 0f
        binding.root.scaleX = 0.94f
        binding.root.scaleY = 0.94f
        binding.root.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(280).start()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(batteryReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(batteryReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
