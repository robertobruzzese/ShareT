package it.sapienza.macc.sharet.customdialogresource

import android.content.DialogInterface
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
import it.sapienza.macc.sharet.database.SharedResourceDatabase
import it.sapienza.macc.sharet.databinding.FragmentCustomDialogResourceBinding

class CustomDialogResourceFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentCustomDialogResourceBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_custom_dialog_resource, container, false)
        val application = requireNotNull(this.activity).application


        //Create an instance of the ViewModelFactory
        val dataSource = SharedResourceDatabase.getInstance(application).sharedResourceDatabaseDao
        val viewModelFactory = CustomDialogResourceViewModelFactory(dataSource)

        //Get a reference to the ViewModel associated with this fragment
        val customDialogViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(CustomDialogResourceViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.customDialogResourceViewModel = customDialogViewModel


        customDialogViewModel.navigateToSharedResource.observe(viewLifecycleOwner) {
            if (it == true) {
                customDialogViewModel.doneNavigating()
            }
        }


        binding.submitCreateResourceButton.setOnClickListener { view: View ->
            customDialogViewModel.onSetSharedResourceName(binding.nameResource.text.toString())

            this.findNavController().navigate(CustomDialogResourceFragmentDirections.actionCustomDialogToSharedResourceFragment())
        }

        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }


}