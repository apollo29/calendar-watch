package com.apollo29.calendarwatch.ui.calendar

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollo29.calendarwatch.R
import com.apollo29.calendarwatch.databinding.FragmentCalendarBinding
import com.apollo29.calendarwatch.model.DTOCalendar
import com.apollo29.calendarwatch.repository.Preferences
import com.apollo29.calendarwatch.ui.main.MainViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentCalendarBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarIcon.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }

        val adapter = CalendarAdapter(Preferences(requireContext())).apply {
            submitList(loadCalendars())
        }
        binding.calendarList.adapter = adapter
    }

    @SuppressLint("Recycle")
    private fun loadCalendars(): MutableList<DTOCalendar> {
        val calendars: MutableList<DTOCalendar> = mutableListOf()
        val eventsUriBuilder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
        val contentCalendars = eventsUriBuilder.build()
        val projection = arrayOf("_id", "calendar_displayName", "account_name")
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val cursorCalendars = contentResolver.query(contentCalendars, projection, null, null, null)
        cursorCalendars!!.moveToFirst()
        val calendarsCount = cursorCalendars.count
        for (i in 0 until calendarsCount) {
            Logger.d(cursorCalendars)
            val id = cursorCalendars.getString(2)
            val title = cursorCalendars.getString(0)
            val account = cursorCalendars.getString(1)
            val calendar = DTOCalendar(id, title, account)
            Logger.d(
                "calendar ID: " + calendar.id + " title: " + calendar.title + " account: " + calendar.account
            )
            calendars.add(calendar)
            cursorCalendars.moveToNext()
        }
        calendars.sortWith { lhs, rhs -> lhs.account.compareTo(rhs.account) }
        return calendars
    }
}