package com.kobylinski.dusigrosz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.model.Debeter
import kotlinx.android.synthetic.main.activity_dluznik.*

class DluznikActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dluznik)
        saveNewDebeter()
    }

    private fun saveNewDebeter() {
        id_debeter_button_ok.setOnClickListener({
            var debt = id_debeter_value?.text.toString().trim()
            var nameDebt = id_name_debeter?.text.toString().trim()
            var debtContact = id_debeter_contact?.text.toString().trim()
            if (incorrectDouble(debt)) errorStatement("Proszę wpisać liczbę w pole Dług")
            else if (isEmpty(nameDebt, debtContact)) {
                errorStatement("Prosze uzupełnić pola Nazwa i Kontakt")
            } else {
                var debeter: Debeter = Debeter(nameDebt, debtContact, debt.toDouble())
                save(debeter)
            }
        })
    }

    private fun save(debeter: Debeter) {
        val dBHelp = DatabaseHelper(this)
        val result = dBHelp.inserDebeter(debeter)
        if (result == -1.toLong()) errorStatement("BłĄD DODANIA")
        else {
            errorStatement("DODANO")
            resetView()
        }
    }

    private fun resetView() {
        id_debeter_contact.setText("")
        id_debeter_value.setText("")
        id_name_debeter.setText("")
        id_name_debeter.requestFocus()
    }

    private fun editDebeter(name: String, contact: String, debt: Double) {
        id_debeter_contact.setText(" ")
        id_debeter_value.setText(" ")
        id_name_debeter.setText(" ")
    }


    private fun incorrectDouble(debt: String): Boolean {
        if (debt.toDouble().isNaN()) return true else return false
    }

    private fun errorStatement(st: String) {
        Toast.makeText(
            this,
            st,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isEmpty(nameDebt: String, debtContact: String): Boolean {
        Log.wtf("inNotEmpty", nameDebt + " " + debtContact)
        return nameDebt.isNullOrEmpty() && debtContact.isNullOrEmpty()
    }

    fun onClickToSimulation(view: View) {
        val intent = Intent(this, SymulationActivity::class.java)
        startActivity(intent)
    }


    fun onClickCancel(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
