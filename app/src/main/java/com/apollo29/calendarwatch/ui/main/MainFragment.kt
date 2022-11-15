package com.apollo29.calendarwatch.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.FragmentMainBinding
import com.apollo29.calendarwatch.model.BatteryInfo
import com.apollo29.calendarwatch.model.BatteryInfo.Companion.CHARGING
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateBatteryView()

        viewModel.batteryLevel().observe(viewLifecycleOwner) {
            updateBatteryView(it)
        }

        binding.buttonMyWatch.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }

        binding.toolbarIcon.setOnClickListener {
            findNavController().navigate(R.id.nav_about)
        }
    }

    private fun updateBatteryView() {
        binding.viewBatteryLevel.setViewType(1)
        binding.viewBatteryLevel.setBatteryLevel(0, false)
        binding.layoutCharging.visibility = View.GONE
    }

    private fun updateBatteryView(bi: BatteryInfo) {
        // todo also refactor view
        var z = true
        if (bi.chargingStatus == CHARGING) {
            binding.viewBatteryLevel.setViewType(2)
            binding.layoutCharging.visibility = View.VISIBLE
        } else {
            binding.viewBatteryLevel.setViewType(0)
            binding.layoutCharging.visibility = View.GONE
        }
        /*
        if (this.mBackgroundService == null || !this.mBackgroundService.isBtAvailable() || GlobalPreferences.getFlightModeSwitch()) {
            z = false
        }
         */
        binding.viewBatteryLevel.setBatteryLevel(bi.level, z)
    }
}