package com.apollo29.calendarwatch.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.BuildConfig
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarIcon.setOnClickListener {
            findNavController().navigate(R.id.nav_main)
        }

        binding.buttonInfoWelcome.setOnClickListener {
            findNavController().navigate(R.id.nav_welcome)
        }

        binding.appVersion.text = getString(
            R.string.about_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE.toString()
        )
    }
}