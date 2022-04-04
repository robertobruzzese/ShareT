package it.sapienza.macc.sharet.sharedresource

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.sapienza.macc.sharet.database.SharedResourceDatabaseDao

class SharedResourceViewModelFactory (
        private val dataSource: SharedResourceDatabaseDao,
        private val application: Application) :  ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedResourceViewModel::class.java)) {
            return SharedResourceViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
        }
