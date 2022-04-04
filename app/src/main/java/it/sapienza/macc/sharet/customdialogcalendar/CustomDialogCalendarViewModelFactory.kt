package it.sapienza.macc.sharet.customdialogcalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.sapienza.macc.sharet.database.ReservationDatabaseDao

class CustomDialogCalendarViewModelFactory(
    private val dataSource: ReservationDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomDialogCalendarViewModel::class.java)) {
            return CustomDialogCalendarViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}