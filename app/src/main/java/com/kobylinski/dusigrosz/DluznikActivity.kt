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
            var debt = id_debeter_value.text.toString().trim()
            var nameDebt = id_name_debeter.text.toString().trim()
            var debtContact = id_debeter_contact.text.toString().trim()
            if (correctDouble(debt)) errorStatement("Proszę wpisać liczbę w pole Dług")
            if (isNotEmpty(nameDebt, debtContact)) {errorStatement("Prosze uzupełnić pola Imię i Kontakt")}
          var debeter:Debeter = Debeter(nameDebt, debtContact, debt.toDouble())
            save(debeter)
        })
    }

    private fun save(debeter: Debeter) {
        val dBHelp = DatabaseHelper(this)
        dBHelp.inserDebeter(debeter)
    }

    private fun correctDouble(debt: String): Boolean {
        if (debt.toDoubleOrNull() != null) return true else return false
    }

    private fun errorStatement(st: String) {
        Toast.makeText(
            this,
            st,
            Toast.LENGTH_SHORT
        )
    }

    private fun isNotEmpty(nameDebt: String, debtContact: String) =
        nameDebt.length > 0 && debtContact.length > 0

    fun onClickToSimulation(view: View) {
        val intent = Intent(this, SymulationActivity::class.java)
        startActivity(intent)
    }


    fun onClickCancel(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
