package it.sapienza.macc.sharet.sharedresourcecalendar

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import it.sapienza.macc.sharet.R
import it.sapienza.macc.sharet.databinding.*
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import it.sapienza.macc.sharet.database.SharedResourceDatabase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class SharedResourceCalendarFragment : Fragment() {




    private val eventsAdapter = SharedResourceCalendarEventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.calendar_dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }


    private val titleRes: Int = R.string.calendar_title

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()

    private lateinit var binding: FragmentSharedResourceCalendarBinding


    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View?  {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shared_resource_calendar,
            container, false)

        binding.exThreeRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        initPrefs()

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        binding.exThreeCalendar.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            binding.exThreeCalendar.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.exThreeCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundResource(R.drawable.calendar_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.example_3_black)
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        binding.exThreeCalendar.monthScrollListener = {
            binding.calendarToolbar.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.exThreeCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                /*if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(R.color.example_3_black)
                    }
                }*/
            }
        }




        val application = requireNotNull(this.activity).application

        val dataSource = SharedResourceDatabase.getInstance(application).sharedResourceDatabaseDao

        val viewModelFactory = SharedResourceCalendarViewModelFactory(dataSource)

        val sharedResourceCalendarViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SharedResourceCalendarViewModel::class.java)

        binding.sharedResourceCalendarViewModel = sharedResourceCalendarViewModel

        binding.lifecycleOwner = this


        binding.exThreeAddButton.setOnClickListener {
            showCustomDialog()
        }

        return binding.root
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(text: String, startTime: String, endTime: String): Boolean {
        if (text.isBlank() or startTime.equals("null") or endTime.equals("null")) {
            Toast.makeText(requireContext(), R.string.fill_fields, Toast.LENGTH_LONG).show()
            return false
        } else {
            selectedDate?.let {
                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it, startTime, endTime))
                updateAdapterForDate(it)
            }
            return true
        }
    }

    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(this@SharedResourceCalendarFragment.events[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    override fun onStart() {
        super.onStart()
        binding.calendarToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.example_3_toolbar_color))
        /*(requireActivity() as AppCompatActivity).setSupportActionBar(binding.calendarToolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)*/
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_3_statusbar_color)
    }

    override fun onStop() {
        super.onStop()
        binding.calendarToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.colorPrimary))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }


    private fun initPrefs() {
        preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString("reservation_name", "null")
        editor.putString("start_time", "null")
        editor.putString("end_time", "null")
        editor.apply()
    }

    private fun showCustomDialog() {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.fragment_custom_dialog_calendar)

        dialog.window?.setBackgroundDrawableResource(R.drawable.round_corner)


        val startTimePicker: TimePickerDialog
        val endTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        startTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val startTime = dialog.getCustomView().findViewById(R.id.selected_start_time) as EditText
                var minuteStr = if(minute<10) '0'+String.format("%d", minute) else String.format("%d", minute)
                var hourStr = if(hourOfDay<10) '0'+String.format("%d", hourOfDay) else String.format("%d", hourOfDay)
                editor.putString("start_time",hourStr+':'+minuteStr)
                editor.apply()
                startTime.setText(preferences.getString("start_time", null)!!)
            }
        }, hour, minute, true)

        endTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val endTime = dialog.getCustomView().findViewById(R.id.selected_end_time) as EditText
                var minuteStr = if(minute<10) '0'+String.format("%d", minute) else String.format("%d", minute)
                var hourStr = if(hourOfDay<10) '0'+String.format("%d", hourOfDay) else String.format("%d", hourOfDay)
                editor.putString("end_time",hourStr+':'+minuteStr)
                editor.apply()
                endTime.setText(preferences.getString("end_time", null)!!)
            }
        }, hour, minute, true)


        /*val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select Reservation Time")
            .build()*/



        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener{

            preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            editor = preferences.edit()

            val reservationName = dialog.getCustomView().findViewById(R.id.name_event) as EditText



            preferences.edit().putString("reservation_name", reservationName.text.toString()).apply()



            //saveEvent(reservationName.text.toString())
            val isWorking = saveEvent(preferences.getString("reservation_name", null)!!,
                      preferences.getString("start_time", null)!!,
                      preferences.getString("end_time", null)!!)

            if(isWorking) dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener {
            preferences.edit().putString("reservation_name", "null").apply()
            preferences.edit().putString("start_time", "null").apply()
            preferences.edit().putString("end_time", "null").apply()
            dialog.dismiss()
        }

        dialog.findViewById<EditText>(R.id.selected_start_time).setOnClickListener {
            startTimePicker.show()
            //picker.show(requireFragmentManager(), "tag")
        }

        dialog.findViewById<EditText>(R.id.selected_end_time).setOnClickListener {
            endTimePicker.show()
            //picker.show(requireFragmentManager(), "tag")
        }




        dialog.show()


    }

}
