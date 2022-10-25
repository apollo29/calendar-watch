package com.apollo29.calendarwatch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.ble.GattService.Companion.RSSI_VALUE
import com.apollo29.calendarwatch.databinding.DialogCalibrateBinding
import com.apollo29.calendarwatch.databinding.FragmentMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.logger.Logger
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*


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

        // TEST

        val now = Calendar.getInstance()
        val value = byteArrayOf(
            (now[1] % 100).toByte(),
            (now[2] + 1).toByte(),
            now[5].toByte(),
            now[11].toByte(),
            now[12].toByte(),
            now[13].toByte(),
            0,
            0
        )
        Logger.d("time")
        Logger.d(value)
        Logger.d(now)

        // endregion

        binding.scanStart.setOnClickListener { startScanning() }
        binding.scanStop.setOnClickListener { stopScanning() }
        binding.calibrate.setOnClickListener {
            calibrate()
        }
        viewModel.batteryLevel().observe(viewLifecycleOwner) {
            binding.batteryLevelInfo.text = it.toString()
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

    fun calibrate() {
        viewModel.manager.calibrateStart()

        val now = Calendar.getInstance()
        val calibrateBinding = DialogCalibrateBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(calibrateBinding.root)
        builder.setOnCancelListener {
            Logger.d("Cancel")
            viewModel.manager.calibrateCancel()
        }
        builder.setPositiveButton(R.string.ok) { dialogInterface, _ ->
            //WatchSettingsActivity.this.mBackgroundService.calibrateWatch(pickerHour.getValue(), pickerMinute.getValue(), 0, pickerSecond.getValue(), 0);
            //
            val hour = calibrateBinding.pickerHour.value
            val minute = calibrateBinding.pickerMinute.value
            val second = calibrateBinding.pickerSecond.value
            Logger.d(
                "Ok! h(%s):m(%s):s(%s)",
                calibrateBinding.pickerHour.value,
                calibrateBinding.pickerMinute.value,
                calibrateBinding.pickerSecond.value
            )
            viewModel.manager.calibrateWatch(hour, minute, 0, second, 0)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialogInterface, _ ->
            Logger.d("Cancel")
            viewModel.manager.calibrateCancel()
            dialogInterface.cancel()
        }

        calibrateBinding.pickerHour.value = now.get(11)
        calibrateBinding.pickerMinute.value = now.get(12)

        builder.create()
        builder.show()
    }
}