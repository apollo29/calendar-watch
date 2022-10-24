package com.apollo29.calendarwatch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apollo29.calendarwatch.MainActivity
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.ble.GattService.Companion.RSSI_VALUE
import com.apollo29.calendarwatch.ble.GattService.Companion.UUID_CHARACTERISTIC_BATTERY_LEVEL
import com.apollo29.calendarwatch.databinding.FragmentMainBinding
import com.orhanobut.logger.Logger
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    private val scanner = BluetoothLeScannerCompat.getScanner()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scanStart.setOnClickListener { startScanning() }
        binding.scanStop.setOnClickListener { stopScanning() }
        binding.batteryLevel.setOnClickListener {
            val deviceAPI = (requireActivity() as MainActivity).gattServiceConn?.binding
            deviceAPI?.apply {
                setMyCharacteristicValue(UUID_CHARACTERISTIC_BATTERY_LEVEL.toString())
            }
        }
        viewModel.batteryLevel().observe(viewLifecycleOwner) {
            binding.batteryLevelInfo.text = it.toString()
        }
    }

    fun startScanning() {
        Logger.d("start scanning")
        scanner.startScan(leScanCallback)
    }

    fun stopScanning() {
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
                /*
                val deviceAPI = (requireActivity() as MainActivity).gattServiceConn?.binding
                deviceAPI?.apply {
                    connect(result.device)
                    stopScanning()
                }
                 */
                viewModel.connect(result.device)
                stopScanning()
            }
        }
    }
}