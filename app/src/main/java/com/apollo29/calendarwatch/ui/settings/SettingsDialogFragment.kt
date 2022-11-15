package com.apollo29.calendarwatch.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.DialogAlertBinding
import com.apollo29.calendarwatch.databinding.DialogCalibrateBinding
import com.apollo29.calendarwatch.databinding.DialogFixedModeBinding
import com.apollo29.calendarwatch.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingsDialogFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsDialogViewModel

    private val onSwitchModeClickListener: View.OnClickListener = View.OnClickListener {
        Logger.d("onSwitchModeClickListener")
        if (it.id == binding.fixedButton.id) {
            Logger.d("show alter dialog")
            fixedMode()
        } else if (it.id == binding.flexibleButton.id) {
            viewModel.switchMode(false, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SettingsDialogViewModel::class.java]
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarIcon.setOnClickListener {
            findNavController().navigate(R.id.nav_main)
        }

        binding.fixedButton.setOnClickListener(onSwitchModeClickListener)
        binding.flexibleButton.setOnClickListener(onSwitchModeClickListener)
        viewModel.switchModeValue.observe(viewLifecycleOwner) {
            if (it) {
                binding.fixedCircle.setColorFilter(resources.getColor(R.color.colorAccent, null))
                binding.fixedButton.setBackgroundColor(
                    resources.getColor(
                        R.color.button_normal,
                        null
                    )
                )

                binding.flexibleCircle.setColorFilter(
                    resources.getColor(
                        R.color.colorPressItem,
                        null
                    )
                )
                binding.flexibleButton.setBackgroundColor(
                    resources.getColor(
                        R.color.dark_activity_background,
                        null
                    )
                )
            } else {
                binding.flexibleCircle.setColorFilter(resources.getColor(R.color.colorAccent, null))
                binding.flexibleButton.setBackgroundColor(
                    resources.getColor(
                        R.color.button_normal,
                        null
                    )
                )

                binding.fixedCircle.setColorFilter(resources.getColor(R.color.colorPressItem, null))
                binding.fixedButton.setBackgroundColor(
                    resources.getColor(
                        R.color.dark_activity_background,
                        null
                    )
                )
            }
        }

        // calibrate
        binding.buttonCalibrateWatch.setOnClickListener {
            calibrate()
        }

        // manual sync
        binding.buttonManualSync.setOnClickListener {
            viewModel.manager.manualSync()
        }

        // select calendars
        binding.buttonCalendars.setOnClickListener {
            findNavController().navigate(R.id.nav_calendar)
        }

        // all day events

        // virbate alert

        // air plane mode
        binding.switchAirplane.setOnCheckedChangeListener { _, value ->
            viewModel.airplaneMode(value)
        }

        // reset
        binding.buttonReset.setOnClickListener {
            viewModel.reset()
        }

        // unpair
        binding.buttonForget.setOnClickListener {
            unpair()
        }
    }

    private fun calibrate() {
        viewModel.manager.calibrateStart()

        val now = Calendar.getInstance()
        val calibrateBinding = DialogCalibrateBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(calibrateBinding.root)
        builder.setOnCancelListener {
            viewModel.manager.calibrateCancel()
        }
        val dialog = builder.create()

        calibrateBinding.buttonOk.setOnClickListener {
            val hour = calibrateBinding.pickerHour.value
            val minute = calibrateBinding.pickerMinute.value
            val second = calibrateBinding.pickerSecond.value
            viewModel.manager.calibrateWatch(hour, minute, 0, second, 0)
            dialog.dismiss()
        }
        calibrateBinding.buttonCancel.setOnClickListener {
            viewModel.manager.calibrateCancel()
            dialog.cancel()
        }

        calibrateBinding.pickerHour.value = now.get(11)
        calibrateBinding.pickerMinute.value = now.get(12)

        dialog.show()
    }

    private fun fixedMode() {
        val fixedModeBinding = DialogFixedModeBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(fixedModeBinding.root)
        val dialog = builder.create()

        fixedModeBinding.buttonOk.setOnClickListener {
            val hour = fixedModeBinding.pickerHour.value
            viewModel.switchMode(true, hour)
            dialog.dismiss()
        }
        fixedModeBinding.buttonCancel.setOnClickListener {
            dialog.cancel()
        }

        fixedModeBinding.pickerHour.value = viewModel.fixedModeValue()

        dialog.show()
    }

    private fun unpair() {
        val alertDialogBinding = DialogAlertBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(alertDialogBinding.root)
        val dialog = builder.create()

        alertDialogBinding.alertDialogText.text = getString(R.string.dialog_msg_forget_watch)
        alertDialogBinding.okButton.text = getString(R.string.yes)
        alertDialogBinding.okButton.setOnClickListener {
            viewModel.unpair()
            dialog.dismiss()
        }
        alertDialogBinding.cancelButton.text = getString(R.string.no)
        alertDialogBinding.cancelButton.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }
}