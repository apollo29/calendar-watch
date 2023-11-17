package com.apollo29.calendarwatch

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.Manifest.permission.READ_CALENDAR
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.apollo29.calendarwatch.ble.DeviceAPI
import com.apollo29.calendarwatch.ble.GattService
import com.apollo29.calendarwatch.databinding.ActivityMainBinding
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var gattServiceConn: GattServiceConn? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // permissions
        requirePermissionsAndStartService()

        // Logger
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .tag("*What*")
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    // PERMISSIONS

    private fun requirePermissionsAndStartService() {
        val permissionRequest = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permissionsBuilder(
                READ_CALENDAR,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            ).build()
        } else {
            permissionsBuilder(
                READ_CALENDAR,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                BLUETOOTH_ADVERTISE,
                BLUETOOTH_SCAN,
                BLUETOOTH_CONNECT
            ).build()
        }

        permissionRequest.send { result ->
            if (result.allGranted()) {
                startService()
            }
            else {
                Snackbar.make(
                    binding.root,
                    "No Permissions Granted, App can't be used",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    // endregion

    private fun startService() {
        // Startup our Bluetooth GATT service explicitly so it continues to run even if
        // this activity is not in focus
        startForegroundService(Intent(this, GattService::class.java))
    }

    override fun onStart() {
        super.onStart()

        val latestGattServiceConn = GattServiceConn()
        if (bindService(Intent(this, GattService::class.java), latestGattServiceConn, 0)) {
            gattServiceConn = latestGattServiceConn
        }
    }

    override fun onStop() {
        super.onStop()

        if (gattServiceConn != null) {
            unbindService(gattServiceConn!!)
            gattServiceConn = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // We only want the service around for as long as our app is being run on the device
        stopService(Intent(this, GattService::class.java))
    }

    // GATT

    class GattServiceConn : ServiceConnection {
        var binding: DeviceAPI? = null

        override fun onServiceDisconnected(name: ComponentName?) {
            binding = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binding = service as? DeviceAPI
        }
    }

    // endregion
}