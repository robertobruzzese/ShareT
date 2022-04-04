package it.sapienza.macc.sharet.customdialogcalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.sapienza.macc.sharet.database.Reservation
import it.sapienza.macc.sharet.database.ReservationDatabaseDao
import kotlinx.coroutines.launch

class CustomDialogCalendarViewModel(val database: ReservationDatabaseDao) : ViewModel() {

    private val _navigateToSharedResourceCalendar = MutableLiveData<Boolean?>()
    val navigateToSharedResourceCalendar: LiveData<Boolean?>
        get() = _navigateToSharedResourceCalendar

    fun doneNavigating() {
        _navigateToSharedResourceCalendar.value = null
    }


    // In Kotlin, the return@label syntax is used for specifying which function among
    // several nested ones this statement returns from.
    // In this case, we are specifying to return from launch(),
    // not the lambda.
    fun onSetReservation(name: String) {
        viewModelScope.launch {
            val newReservation = Reservation()
            newReservation.reservationName = name
            database.insert(newReservation)

            _navigateToSharedResourceCalendar.value = true
        }
    }

}