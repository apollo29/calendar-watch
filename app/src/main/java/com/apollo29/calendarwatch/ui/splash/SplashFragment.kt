package com.apollo29.calendarwatch.ui.splash

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_VIEW_START
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.DialogAlertBinding
import com.apollo29.calendarwatch.databinding.FragmentSplashBinding
import com.apollo29.calendarwatch.model.PairingStatus
import com.apollo29.calendarwatch.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pairing.observe(viewLifecycleOwner) {
            if (it == PairingStatus.SUCCESS) {
                Toast.makeText(requireContext(), "Successfully reconnected", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            } else if (it == PairingStatus.FAILURE) {
                Toast.makeText(requireContext(), "Failure on reconnecting", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            }
        }

        if (viewModel.isFirstShow()) {
            agreement()
        } else {
            // todo reconnect
            if (viewModel.watchId() != null) {
                // todo add timeout and maybe spinner
                viewModel.reconnect()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            }
        }
    }

    private fun agreement() {
        val alertDialogBinding = DialogAlertBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(alertDialogBinding.root)
        val dialog = builder.create()

        alertDialogBinding.alertDialogTitle.text = getString(R.string.about_app_name)
        alertDialogBinding.alertDialogTitle.visibility = VISIBLE
        alertDialogBinding.alertDialogText.textAlignment = TEXT_ALIGNMENT_VIEW_START
        alertDialogBinding.alertDialogText.gravity = Gravity.START
        alertDialogBinding.alertDialogText.text = getString(R.string.dialog_terms_agreement)
        alertDialogBinding.okButton.text = getString(R.string.dialog_terms_agree)
        alertDialogBinding.okButton.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            dialog.dismiss()
        }
        alertDialogBinding.cancelButton.text = getString(R.string.dialog_terms_disagree)
        alertDialogBinding.cancelButton.setOnClickListener {
            dialog.cancel()
            finishAffinity(requireActivity())
        }

        dialog.show()
    }
}