package com.apollo29.calendarwatch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.ble.GattService.Companion.RSSI_VALUE
import com.apollo29.calendarwatch.databinding.FragmentMainBinding
import com.apollo29.calendarwatch.model.BatteryInfo
import com.apollo29.calendarwatch.model.BatteryInfo.Companion.CHARGING
import com.orhanobut.logger.Logger
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private val scanner = BluetoothLeScannerCompat.getScanner()

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

        binding.scanStart.setOnClickListener { startScanning() }
        binding.scanStop.setOnClickListener { stopScanning() }

        viewModel.batteryLevel().observe(viewLifecycleOwner) {
            binding.batteryLevelInfo.text = it.toString()
            updateBatteryView(it)
        }

        binding.buttonMyWatch.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }
    }

    private fun startScanning() {
        Logger.d("start scanning")
        scanner.startScan(leScanCallback)
    }

    private fun stopScanning() {
        Logger.d("stopping scanning")
        scanner.stopScan(leScanCallback)
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (!result.device.name.isNullOrEmpty() && result.device.name.lowercase()
                    .startsWith("gw_")
            ) {
                Logger.d("Device Name ${result.device.name} rssi: ${result.rssi}")
                Logger.d("RSSI %s", result.rssi > RSSI_VALUE)
                viewModel.connect(result.device)
                stopScanning()
            }
        }
    }

    fun updateBatteryView() {
        binding.viewBatteryLevel.setViewType(1)
        binding.viewBatteryLevel.setBatteryLevel(0, false)
        binding.layoutCharging.visibility = View.GONE
    }

    fun updateBatteryView(bi: BatteryInfo) {
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