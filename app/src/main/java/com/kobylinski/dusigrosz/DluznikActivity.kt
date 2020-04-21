package com.kobylinski.dusigrosz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        val isInDB:Long = setValuesDebterActivity()
        saveDebeter(isInDB)
    }

    private fun setValuesDebterActivity(): Long {
        val name = intent?.getStringExtra("name")
        val contact = intent?.getStringExtra("phone")
        val debt = intent?.getDoubleExtra("debt", 0.0)
        val db = DatabaseHelper(this)
        val isNotNull=!isEditable(name.orEmpty(), contact.orEmpty())
       var id:Long=-1
        if (isNotNull) {
            id_debeter_button_ok.setText("Zmień")
            id_debeter_contact.setText(contact)
            id_debeter_value.setText(debt.toString())
            id_name_debeter.setText(name)
            id= db.getId(name.toString(), contact.toString())
        }
        Log.wtf("id",id.toString())
        return id as Long
    }


    private fun isEditable(nameDebt: String, contact: String): Boolean =
        nameDebt.isNullOrEmpty() || contact.isNullOrEmpty()


    private fun saveDebeter(inDB: Long) {
        val isInDatabase = id_debeter_button_ok.text.equals("Zmień")
        id_debeter_button_ok.setOnClickListener({
            var debt = id_debeter_value.text.toString().trim()
            var nameDebt = id_name_debeter.text.toString().trim()
            var debtContact = id_debeter_contact.text.toString().trim()
            if (!isInDatabase) {
                saveNewDebter(debt, nameDebt, debtContact)
            } else {
                updateDebeter(nameDebt, debt, debtContact, inDB)
            }
        })
    }

    private fun updateDebeter(
        nameDebt: String,
        debt: String,
        debtContact: String,
        inDB: Long
    ) {
        val dBHelp = DatabaseHelper(this)
        val UpdateOk = dBHelp?.updateDebeterData(nameDebt, debt.toDouble(), debtContact, inDB)
        if (UpdateOk!!) {
            errorStatement("ZAKTUALIZOWANO")

            onClickCancel(findViewById(R.id.id_debeter_button_ok))
        } else errorStatement("BŁĄD AKTUALIZACJI")
    }

    private fun saveNewDebter(
        debt: String,
        nameDebt: String,
        debtContact: String
    ) {
        if (incorrectDouble(debt)) errorStatement("Proszę wpisać liczbę w pole Dług")
        else if (isEmpty(nameDebt, debtContact)) {
            errorStatement("Prosze uzupełnić pola Nazwa i Kontakt")
        } else {
            var debeter: Debeter = Debeter(nameDebt, debtContact, debt.toDouble())
            save(debeter)
        }
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
        if (debt?.toDouble().isNaN()) return true else return false
    }

    private fun errorStatement(st: String) {
        Toast.makeText(
            this,
            st,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isEmpty(nameDebt: String, debtContact: String): Boolean {
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
