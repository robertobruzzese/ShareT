package it.sapienza.macc.sharet.sharedresourcecalendar

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import it.sapienza.macc.sharet.database.SharedResource
import it.sapienza.macc.sharet.database.SharedResourceDatabaseDao
import it.sapienza.macc.sharet.network.SharedResourceApi
import it.sapienza.macc.sharet.network.SharedResourceProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedResourceCalendarViewModel(
    //private val sharedResourceKey: Long = 0L,
    dataSource: SharedResourceDatabaseDao): ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val database = dataSource
    /*
    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()
    */
    private val resource = MediatorLiveData<SharedResource>()

    fun getResource() = resource


    private val _navigateToCustomDialogCalendar = MutableLiveData<Boolean>()
    val navigateToCustomDialogCalendar: LiveData<Boolean>
        get() = _navigateToCustomDialogCalendar


    fun doneNavigating() {
        _navigateToCustomDialogCalendar.value = null
    }

    fun onAddReservationButton() {
        viewModelScope.launch {

            _navigateToCustomDialogCalendar.value = true
        }
    }



    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    //Create a coroutine Job and a CoroutineScope using the main dispatcher
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    init {
        //resource.addSource(database.getResourceWithId(sharedResourceKey), resource::setValue)
        getSharedResourceProperties()
    }

    private fun getSharedResourceProperties() {
        coroutineScope.launch {
            var getPropertiesDeferred = SharedResourceApi.retrofitService.getProperties()
            try {
                var listResult = getPropertiesDeferred.await()
                _response.value = "Name: ${listResult.name} - Id: ${listResult.id}"
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

/*
    *//**
     * Variable that tells the fragment whether it should navigate to [SleepTrackerFragment].
     *
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the [Fragment]
     *//*
    private val _navigateToSharedResource = MutableLiveData<Boolean?>()

    *//**
     * When true immediately navigate back to the [SleepTrackerFragment]
     *//*
    val navigateToSharedResource: LiveData<Boolean?>
        get() = _navigateToSharedResource


    *//**
     * Call this immediately after navigating to [SleepTrackerFragment]
     *//*
    fun doneNavigating() {
        _navigateToSharedResource.value = null
    }

    fun onClose() {
        _navigateToSharedResource.value = true
    }*/
}