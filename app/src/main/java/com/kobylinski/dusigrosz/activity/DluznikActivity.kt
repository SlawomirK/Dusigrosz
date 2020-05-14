package com.kobylinski.dusigrosz.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.View
import com.kobylinski.dusigrosz.MainActivity
import com.kobylinski.dusigrosz.R
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.helpers.CreateDialog.Companion.showToast
import com.kobylinski.dusigrosz.model.Debeter
import kotlinx.android.synthetic.main.activity_dluznik.*


class DluznikActivity : AppCompatActivity() {
    private var debeter = Debeter("", "", 0.0)
    private lateinit var db: DatabaseHelper
    private var wasSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dluznik)
        db = DatabaseHelper.openDatabase(this)
        val isInDB: Long = setValuesDebterActivity()
        saveDebeter(isInDB)
    }

    private fun setValuesDebterActivity(): Long {
        val name = intent?.getStringExtra("name")
        val contact = intent?.getStringExtra("phone")
        val debt = intent.getDoubleExtra("debt", 0.0)

        val isNotNull = isEditable(name.orEmpty(), contact.orEmpty())
        var id: Long = -1
        if (isNotNull) {
            id_debeter_button_ok.text = "Zmień"
            id_debeter_contact.setText(contact)
            id_debeter_value.setText(debt.toString())
            id_name_debeter.setText(name)
            this.debeter = Debeter(name.toString(), contact.toString(), debt)
            id = db.getId(this.debeter)
        }
        return id
    }

    private fun isEditable(nameDebt: String, contact: String): Boolean =
        nameDebt.isNotEmpty() and (contact.isNotEmpty())

    private fun saveDebeter(inDB: Long) {
        val isInDatabase = id_debeter_button_ok.text == "Zmień"
        id_debeter_button_ok.setOnClickListener {
            var debt = id_debeter_value.text.toString().trim()
            var nameDebt = id_name_debeter.text.toString().trim()
            var debtContact = id_debeter_contact.text.toString().trim()
            val isEdit = isEditable(nameDebt, debtContact)

            if (isEdit) {
                this.debeter = Debeter(nameDebt, debtContact, debt.toDouble())
                if (!isInDatabase) {
                    save(this.debeter)
                } else {
                    updateDebeter(nameDebt, debt, debtContact, inDB)
                }
                id_debeter_button_cancel.text = "Powrót"
            } else {
                wrongDebetersFields()
            }
        }
    }

    private fun updateDebeter(nameDebt: String, debt: String, debtContact: String, inDB: Long) {

        val UpdateOk = db.updateDebeterData(nameDebt, debt.toDouble(), debtContact, inDB)
        if (UpdateOk!!) {
            showToast("ZAKTUALIZOWANO", this)
        } else showToast("BŁĄD AKTUALIZACJI", this)
    }

    fun wrongDebetersFields() {
        showToast("Uzupełnij wymagane dane", this)
        id_name_debeter.setHintTextColor(Color.RED)
        id_debeter_contact.setHintTextColor(Color.RED)

    }

    private fun checkDepterToSave(debt: String, nameDebt: String, debtContact: String): Boolean {
        val incorect = incorrectDouble(debt)
        var debeter: Debeter = Debeter("", "", 0.0)
        var debterIsCorrect = false
        if (incorect) showToast("Proszę wpisać liczbę w pole Dług", this)
        else if (isEmpty(nameDebt, debtContact)) {
            showToast("Prosze uzupełnić pola Nazwa i Kontakt", this)
        } else {
            debterIsCorrect = true
        }
        return debterIsCorrect
    }

    private fun save(debeter: Debeter) {
        if (checkDepterToSave(debeter.debt.toString(), debeter.name, debeter.phone)) {
            val result = db.inserDebeter(debeter)
            val isInDB = setValuesDebterActivity()

            if (isInDB != (-1).toLong()) showToast("Dłużnik już jest w bazie", this)
            else if (result == (-1).toLong()) showToast("Błąd dodania", this)
            else {
                showToast("DODANO", this)
                wasSaved = true
            }
        }
    }


    private fun incorrectDouble(debt: String): Boolean {
        return !debt.isNullOrEmpty().or(!debt.toDouble().isNaN())
    }

    private fun isEmpty(nameDebt: String, debtContact: String): Boolean {
        return nameDebt.isNullOrEmpty() || debtContact.isNullOrEmpty()
    }

    fun onClickToSimulation(view: View) {
        val intent = Intent(this, SymulationActivity::class.java)
        val ir = id_name_debeter.text.toString()
        val debt = id_debeter_value.text.toString()
        if (ir.isNotEmpty() and debt.isNotEmpty()) {
            intent.putExtra("name", ir)
            intent.putExtra("value", debt)
            startActivity(intent)
        } else {
            showToast("pola name i value w tym przypadku nie mogą być puste", this)
        }
    }


    fun onClickCancel(view: View) {
        if (id_debeter_button_cancel.text.toString().equals("Powrót", ignoreCase = true)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (areNotEmpty().or(!wasSaved)) {
                createDialogOnNotEmptyCancel()
            } else
                startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun onClickSendSms(view: View) {
        val debt = id_debeter_value.text.toString()
        val textMessage = "Potwierdzenie, że pożyczyłeś ode mnie $debt zł"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, textMessage)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Udostępnij tekst w:"))

        // zgody na wysyłanie sms- wersja niezgodna ze specyfikacją
        /* val permisionCheck = checkSelfPermission(this, Manifest.permission.SEND_SMS)
         if (permisionCheck == PackageManager.PERMISSION_GRANTED) {
             whenPermissionGranted()
         } else {
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 0)
         }*/
    }

    private fun whenPermissionGranted() {
        val phone = id_debeter_contact.text.toString()
        val debt = id_debeter_value.text.toString()

        val debterComplete = checkDepterToSave(
            debt,
            id_name_debeter.text.toString(),
            phone
        )
        val obj = SmsManager.getDefault()
        if (debterComplete) {
            val telNumber = phone
            val debt = debt
            val textMessage = "Pożyczyłeś od właściciela tego numer $debt zł"
            obj.sendTextMessage(telNumber, null, textMessage, null, null)
            showToast("Wysłano potwierdzenie", this)
        } else showToast("Nie wysłano", this)
    }

    private fun createDialogOnNotEmptyCancel() {
        val title = "W formularzu są dane."
        val butOk = "Rezygnuje"
        val message = "Czy na pewno chcesz zrezygnować z edycji?"
        val cancel = "Wróć do Edycji"

        val dialog = createDialog(title, message, butOk, cancel).create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            0 -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    whenPermissionGranted()
                else {
                    showToast("Wymagana zgoda na wysyłanie SMS", this)
                }
            }
            else -> showToast("Nie dałeś zgody na wysyłanie SMS", this)
        }
    }

    private fun createDialog(
        title: String,
        body: String,
        butOk: String,
        ButCancel: String
    ): AlertDialog.Builder {
        val intent = Intent(this, MainActivity::class.java)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(body)
        builder.setPositiveButton(butOk) { _, _ ->
            run {
                startActivity(intent)
            }
        }
        builder.setNegativeButton(ButCancel) { _, _ -> }
        return builder
    }

    private fun areNotEmpty(): Boolean {
        val isEmpty =
            id_debeter_contact.text.isEmpty().or(id_debeter_value.text.isEmpty())
                .or(id_debeter_contact.text.isEmpty())
        return !isEmpty
    }
}
