package com.apollo29.calendarwatch.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.FragmentSplashBinding
import com.apollo29.calendarwatch.model.PairingStatus
import com.apollo29.calendarwatch.ui.main.MainViewModel
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
            if (it==PairingStatus.SUCCESS) {
                Toast.makeText(requireContext(), "Successfully reconnected", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            }
            else if (it==PairingStatus.FAILURE) {
                Toast.makeText(requireContext(), "Failure on reconnecting", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            }
        }

        if (viewModel.isFirstShow()) {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        } else {
            // todo reconnect
            if (viewModel.watchId() != null) {
                viewModel.reconnect()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            }
        }
    }
}