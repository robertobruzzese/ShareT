package it.sapienza.macc.sharet.sharedresourcecalendar

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import it.sapienza.macc.sharet.databinding.CalendarEventItemViewBinding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
class SharedResourceCalendarEventsAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<SharedResourceCalendarEventsAdapter.SharedResourceCalendarEventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedResourceCalendarEventsViewHolder {
        return SharedResourceCalendarEventsViewHolder(
            CalendarEventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: SharedResourceCalendarEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class SharedResourceCalendarEventsViewHolder(private val binding: CalendarEventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(events[bindingAdapterPosition])
            }
        }


        fun bind(event: Event) {
            binding.itemEventText.text = event.text
            /*binding.startTime.text = event.startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            binding.endTime.text = event.endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))*/
            binding.startTime.text = event.startTime
            binding.endTime.text = event.endTime
        }
    }
}