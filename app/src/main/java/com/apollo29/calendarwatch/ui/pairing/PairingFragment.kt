package com.apollo29.calendarwatch.ui.pairing

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.ble.GattService
import com.apollo29.calendarwatch.databinding.FragmentGettingStartedBinding
import com.apollo29.calendarwatch.ui.main.MainViewModel
import com.orhanobut.logger.Logger
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

class PairingFragment : Fragment() {
    private var _binding: FragmentGettingStartedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private val scanner = BluetoothLeScannerCompat.getScanner()
    private val settings: ScanSettings = ScanSettings.Builder()
        .setLegacy(false)
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .setReportDelay(5000)
        .setUseHardwareBatchingIfSupported(true)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        _binding = FragmentGettingStartedBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPair.setOnClickListener {
            it.isEnabled = false
            startScanning()
        }
    }

    private fun startScanning() {
        binding.progressPairWatch.visibility = VISIBLE
        Logger.d("start scanning")
        //scanner.startScan(mutableListOf(), settings, leScanCallback)
        scanner.startScan(leScanCallback)
    }

    private fun stopScanning() {
        binding.progressPairWatch.visibility = INVISIBLE
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
                Logger.d("RSSI %s", result.rssi > GattService.RSSI_VALUE)
                viewModel.connect(result.device)
                stopScanning()
                findNavController().navigate(R.id.nav_main)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Logger.d("Scan Failed: $errorCode")
            binding.progressPairWatch.visibility = INVISIBLE
            binding.buttonPair.text = getString(R.string.try_again)
            binding.buttonPair.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopScanning()
    }
}