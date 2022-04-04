package it.sapienza.macc.sharet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import it.sapienza.macc.sharet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    internal lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}