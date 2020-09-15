package com.davinciapp.amindfullquit.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.di.ViewModelFactory
import java.util.*
import kotlin.math.floor

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context): Intent = Intent(context, SettingsActivity::class.java)
    }

    private lateinit var viewModel: SettingsViewModel
    //VALUES
    private var defaultConsumption = 0
    private var previousConsumption = 0
    private lateinit var dateArray: Array<Int>
    private var cigarettesPerPack = 0
    private var packPrice = 0F
    private var currency = "€"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        //VIEW MODEL
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(application))
            .get(SettingsViewModel::class.java)

        viewModel.defaultConsumption.observe(this, Observer {
            defaultConsumption = it
            findViewById<TextView>(R.id.text_view_default_consumption_settings).text = it.toString()
        })

        viewModel.startingDateArray.observe(this, Observer {
            dateArray = it
            val month = GregorianCalendar(it[0], it[1], it[2]).getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            )
            val text = it[2].toString() + " " + month + "\n" + it[0]
            findViewById<TextView>(R.id.text_view_starting_date_settings).text = text
        })

        viewModel.previousConsumption.observe(this, Observer {
            previousConsumption = it
            findViewById<TextView>(R.id.text_view_previous_consumption_settings).text =
                it.toString()
        })

        viewModel.cigarettesPerPack.observe(this, Observer {
            cigarettesPerPack = it
            findViewById<TextView>(R.id.text_view_cigarettes_per_pack_settings).text = it.toString()
        })


        viewModel.currency.observe(this, Observer {
            currency = it
            findViewById<TextView>(R.id.text_view_pack_price_settings).text = "$packPrice$currency"
        })

        viewModel.packPrice.observe(this, Observer {
            packPrice = it
            findViewById<TextView>(R.id.text_view_pack_price_settings).text = "$it$currency"
        })

        //CLICKS
        findViewById<ImageButton>(R.id.image_button_default_number_settings).setOnClickListener {
            openUniqueNumberPickerDialog(it.id)
        }

        findViewById<ImageButton>(R.id.image_button_starting_date_settings).setOnClickListener {
            openDatePickerDialog()
        }

        findViewById<ImageButton>(R.id.image_button_previous_consumption_settings).setOnClickListener {
            openUniqueNumberPickerDialog(it.id)
        }

        findViewById<ImageButton>(R.id.image_button_cigarettes_per_pack_settings).setOnClickListener {
            openUniqueNumberPickerDialog(it.id)
        }

        findViewById<ImageButton>(R.id.image_button_pack_price_settings).setOnClickListener {
            openPricePickerDialog()
        }


    }

    private fun openUniqueNumberPickerDialog(viewId: Int) {

        val picker = NumberPicker(this)
        picker.maxValue = 100
        picker.value = when (viewId) {
            R.id.image_button_default_number_settings -> defaultConsumption
            R.id.image_button_previous_consumption_settings -> previousConsumption
            R.id.image_button_cigarettes_per_pack_settings -> cigarettesPerPack
            else -> 0
        }


        val builder = AlertDialog.Builder(this)
        builder.setView(picker)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _ ->

            when (viewId) {
                R.id.image_button_default_number_settings -> viewModel.updateDefaultCigarettesNumber(
                    picker.value
                )
                R.id.image_button_previous_consumption_settings -> viewModel.updatePreviousCigarettesNumber(
                    picker.value
                )
                R.id.image_button_cigarettes_per_pack_settings -> viewModel.updateCigarettesNumberPerPack(
                    picker.value
                )
            }

            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun openPricePickerDialog() {

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_pack_price_picker, null)
        val unitPicker = view.findViewById<NumberPicker>(R.id.number_picker_unit_pack_price)
        val centsPicker = view.findViewById<NumberPicker>(R.id.number_picker_cents_pack_price)
        val currencySpinner = view.findViewById<Spinner>(R.id.spinner_currency_pack_price)

        val roundPrice = floor(packPrice).toInt()
        unitPicker.apply {
            maxValue = 50
            value = roundPrice
        }
        centsPicker.apply {
            maxValue = 100
            value = ((packPrice - roundPrice) * 100).toInt()
        }

        /*
        ArrayAdapter.createFromResource(
            this,
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        )
            .also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                currencySpinner.adapter = adapter
            }
         */


        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _ ->

            val price = unitPicker.value + (centsPicker.value / 100F)
            viewModel.updatePackPrice(price)

            viewModel.updateCurrency(currencySpinner.selectedItem.toString())

            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun openDatePickerDialog() {

        val currentYear = dateArray[0]
        val currentMonth = dateArray[1]
        val currentDay = dateArray[2]

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            //Careful : month starts at 0 in Calendar
            openConfirmationDialog(year, month, day)
        }

        val dialog = DatePickerDialog(this, listener, currentYear, currentMonth, currentDay)
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.datePicker.minDate = 0L
        dialog.show()
    }

    private fun openConfirmationDialog(year: Int, month: Int, day: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Changing date? \n(Data prior to this will be deleted)")

        builder.setPositiveButton("Ok") { dialog: DialogInterface, _ ->
            viewModel.updateStartingTime(year, month, day)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorAccent))

    }
}
