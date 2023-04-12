package com.kennypewpew.periodtracker

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.kennypewpew.periodtracker.databinding.FragmentSecondBinding
import java.io.File
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
typealias mCalendarView = com.applandeo.materialcalendarview.CalendarView

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        periodData.init(context)

        fun highlightEntries(context: Context?){
            val v = view.findViewById(R.id.calendarView) as mCalendarView

            val dates = periodData.getAllPeriodDays()
            if ( dates.isNotEmpty() )
                v.setHighlightedDays(dates)

            val events = periodData.getAllPeriodEvents(context)
            if ( events.isNotEmpty() )
                v.setEvents(events)
        }

        highlightEntries(context)

        binding.setPeriodStart.setOnClickListener {

            val v = view.findViewById(R.id.calendarView) as mCalendarView

            var dates = v.getSelectedDates() as List<Calendar>
            if ( dates.isNotEmpty() ) {
                dates = dates.sorted()
                val s = dates[0]
                val e = dates.last()

                val fmt = SimpleDateFormat("yyyy/MM/dd")
                val t: String = fmt.format(s.timeInMillis) + "\n" + fmt.format(e.timeInMillis)

                periodData.addPeriod(s, e)
            }
        }


        binding.highlight.setOnClickListener {
            highlightEntries(context)
        }

        binding.setPeriodEnd.setOnClickListener {
            periodData.save(context)
        }

        binding.load.setOnClickListener {
            periodData.load(context)
            val toast = Toast.makeText(context, "loading data", Toast.LENGTH_SHORT)
            toast.show()
        }

        binding.buttonStartPeriod.setOnClickListener {
            if ( periodData.isPeriodActive() ) {
                val alertBuilder = AlertDialog.Builder(context)
                val fmt = SimpleDateFormat("dd/MM/yyyy")
                val current = fmt.format(periodData.getActivePeriodDate().timeInMillis)
                alertBuilder.setMessage("Overwrite current active period ($current)")
                alertBuilder.setPositiveButton("Yes") { dialog, which ->
                    periodData.startPeriod(Calendar.getInstance())
                }
                alertBuilder.setNegativeButton("No") { dialog, which ->
                    return@setNegativeButton
                }
                alertBuilder.show()
            }
            else {
                periodData.startPeriod(Calendar.getInstance())
            }
        }

        binding.buttonGotoList.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}