package it.sapienza.macc.sharet.customdialogcalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.sapienza.macc.sharet.R
import it.sapienza.macc.sharet.database.ReservationDatabase
import it.sapienza.macc.sharet.databinding.FragmentCustomDialogCalendarBinding


class CustomDialogCalendarFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentCustomDialogCalendarBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_custom_dialog_calendar, container, false)

        val application = requireNotNull(this.activity).application


        //Create an instance of the ViewModelFactory
        val dataSource = ReservationDatabase.getInstance(application).ReservationDatabaseDao
        val viewModelFactory = CustomDialogCalendarViewModelFactory(dataSource)

        //Get a reference to the ViewModel associated with this fragment
        val customDialogViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(CustomDialogCalendarViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.customDialogCalendarViewModel = customDialogViewModel


        customDialogViewModel.navigateToSharedResourceCalendar.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                customDialogViewModel.doneNavigating()
            }
        })


        /*binding.submitCreateEventButton.setOnClickListener { view: View ->
            customDialogViewModel.onSetReservation(binding.nameEvent.text.toString())

            this.findNavController().navigate(CustomDialogCalendarFragmentDirections.actionCustomDialogCalendarFragmentToSharedResourceCalendarFragment())
        }*/
/*
        val dialog = getDialog()
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner)*/


        return binding.root
    }


/*
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }*/
}