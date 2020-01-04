package com.example.amindfullquit.settings

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.amindfullquit.R
import com.example.amindfullquit.di.ViewModelFactory
import java.util.*

class SettingsActivity : AppCompatActivity() {

    companion object{
        fun newInstance(context: Context): Intent = Intent(context, SettingsActivity::class.java)
    }

    private lateinit var viewModel: SettingsViewModel
    private var defaultConsumption = 0
    private var previousConsumption = 0
    private lateinit var dateArray: Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        //VIEW MODEL
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(SettingsViewModel::class.java)

        viewModel.defaultConsumption.observe(this, Observer {
            defaultConsumption = it
            findViewById<TextView>(R.id.text_view_default_consumption_settings).text = it.toString()
        })

        viewModel.startingDateArray.observe(this, Observer {
            dateArray = it
            val month = GregorianCalendar(it[0], it[1], it[2]).getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            val text = it[2].toString() + " " + month + "\n" + it[0]
            findViewById<TextView>(R.id.text_view_starting_date_settings).text = text
        })

        viewModel.previousConsumption.observe(this, Observer {
            previousConsumption = it
            findViewById<TextView>(R.id.text_view_previous_consumption_settings).text = it.toString()
        })

        //CLICKS
        findViewById<ImageButton>(R.id.image_button_default_number_settings).setOnClickListener {
            openNumberPickerDialog(it.id)
        }

        findViewById<ImageButton>(R.id.image_button_starting_date_settings).setOnClickListener{
            openDatePickerDialog()
        }

        findViewById<ImageButton>(R.id.image_button_previous_consumption_settings).setOnClickListener {
            openNumberPickerDialog(it.id)
        }


    }

    private fun openNumberPickerDialog(viewId: Int){

        val picker = NumberPicker(this)
        picker.maxValue = 30
        picker.value = when (viewId){
            R.id.image_button_default_number_settings -> defaultConsumption
            R.id.image_button_previous_consumption_settings -> previousConsumption
            else -> 0
        }


        val builder = AlertDialog.Builder(this)
        builder.setView(picker)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _ ->

            when (viewId){
                R.id.image_button_default_number_settings -> viewModel.updateDefaultCigarettesNumber(picker.value)
                R.id.image_button_previous_consumption_settings -> viewModel.updatePreviousCigarettesNumber(picker.value)
            }

            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun openDatePickerDialog(){

        val currentYear = dateArray[0]
        val currentMonth = dateArray[1]
        val currentDay = dateArray[2]

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            //Careful : month starts at 0 in Calendar
            viewModel.updateStartingTime(year, month, day)
        }

        val dialog = DatePickerDialog(this, listener, currentYear, currentMonth, currentDay)
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }
}
