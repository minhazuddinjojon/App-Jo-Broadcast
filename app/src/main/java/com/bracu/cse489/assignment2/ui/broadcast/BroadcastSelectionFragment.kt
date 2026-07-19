package com.bracu.cse489.assignment2.ui.broadcast

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bracu.cse489.assignment2.R
import com.bracu.cse489.assignment2.databinding.FragmentBroadcastSelectionBinding

/**
 * Assignment 2 - Part A, Screen 1/3.
 * Shows a Spinner with the two broadcast options and a Proceed button that
 * routes to the Custom-input screen or the Battery screen depending on the selection.
 * A helper subtitle updates live as the Spinner selection changes.
 */
class BroadcastSelectionFragment : Fragment(R.layout.fragment_broadcast_selection) {

    private var _binding: FragmentBroadcastSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBroadcastSelectionBinding.bind(view)

        val options = resources.getStringArray(R.array.broadcast_type_options)
        binding.spinnerBroadcastType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            options
        )

        binding.spinnerBroadcastType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.tvSubtitle.text = if (position == 0) {
                    getString(R.string.subtitle_custom_broadcast)
                } else {
                    getString(R.string.subtitle_battery_broadcast)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnProceed.setOnClickListener {
            when (binding.spinnerBroadcastType.selectedItemPosition) {
                0 -> findNavController().navigate(
                    BroadcastSelectionFragmentDirections
                        .actionBroadcastSelectionFragmentToCustomBroadcastInputFragment()
                )
                else -> findNavController().navigate(
                    BroadcastSelectionFragmentDirections
                        .actionBroadcastSelectionFragmentToBatteryBroadcastFragment()
                )
            }
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
