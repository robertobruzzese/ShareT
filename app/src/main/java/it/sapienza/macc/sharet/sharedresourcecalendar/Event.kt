package it.sapienza.macc.sharet.sharedresourcecalendar

import java.time.LocalDate
import java.time.LocalTime

data class Event(val id: String, val text: String, val date: LocalDate, val startTime: String, val endTime: String)

//migrate startTime and endTime in LocaLTime