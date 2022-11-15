package com.apollo29.calendarwatch.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.FragmentWelcomeBinding
import com.apollo29.calendarwatch.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wasFirstShow()

        val images = listOf(
            R.drawable.welcome_1,
            R.drawable.welcome_3,
            R.drawable.welcome_4,
            R.drawable.welcome_5,
            R.drawable.welcome_6
        )
        val adapter = WelcomeImageAdapter(images)
        binding.welcomeViewPager.adapter = adapter
        binding.indicator.setViewPager(binding.welcomeViewPager)
        adapter.registerAdapterDataObserver(binding.indicator.adapterDataObserver)

        binding.toolbarIcon.setOnClickListener {
            if (viewModel.watchConnected()) {
                findNavController().navigate(R.id.action_welcomeFragment_to_main)
            }
            else {
                findNavController().navigate(R.id.nav_getting_started)
            }
        }
    }
}