package com.kobylinski.dusigrosz.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import com.kobylinski.dusigrosz.MainActivity
import com.kobylinski.dusigrosz.R
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.helpers.CreateDialog.Companion.showToast
import com.kobylinski.dusigrosz.model.Debeter
import kotlinx.android.synthetic.main.activity_dluznik.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class DluznikActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private var debeter = Debeter("", LocalDate.now(), 0.0)
    private lateinit var db: DatabaseHelper
    private var wasSaved = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dluznik)
        id_debeter_contact.setText(LocalDate.now().toString())
        db = DatabaseHelper.openDatabase(this)
        val isInDB: Long = setValuesDebterActivity()
        saveDebeter(isInDB)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setValuesDebterActivity(): Long {
        val name = intent?.getStringExtra("name")
        val date = intent?.getStringExtra("date")
        val debt = intent?.getDoubleExtra("debt", 0.0)
        val isNotNull = isEditable(name.orEmpty())
        println("is not null " + isNotNull)
        var id: Long = -1
        if (isNotNull) {
            id_debeter_button_ok.text = "Zmień"
            id_debeter_contact.setText(date)
            id_debeter_value.setText(debt.toString())
            id_name_debeter.setText(name)
            Log.d("Ooooooooo", date)
            this.debeter =
                Debeter(name.toString(), LocalDate.parse(id_debeter_contact.text.trim()), debt)
            id = db.getId(this.debeter)
        }
        return id
    }

    private fun isEditable(nameDebt: String): Boolean =
        !nameDebt.isNullOrEmpty()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDebeter(inDB: Long) {
        val isInDatabase = id_debeter_button_ok.text == "Zmień"
        id_debeter_button_ok.setOnClickListener {
            var debt = id_debeter_value.text.toString().trim()
            var nameDebt = id_name_debeter.text.toString().trim()
            var date = id_debeter_contact.text.toString().trim()
            val isEdit = isEditable(nameDebt)

            if (isEdit) {
                this.debeter = Debeter(nameDebt, LocalDate.parse(date), debt.toDouble())
                if (!isInDatabase) {
                    save(this.debeter)
                } else {
                    updateDebeter(nameDebt, debt, LocalDate.parse(date), inDB)
                }
                id_debeter_button_cancel.text = "Powrót"
            } else {
                wrongDebetersFields()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDebeter(nameDebt: String, debt: String, date: LocalDate, inDB: Long) {

        val updateOk = db.updateDebeterData(nameDebt, debt.toDouble(), date.toString(), inDB)
        if (updateOk!!) {
            showToast("ZAKTUALIZOWANO", this)
        } else showToast("BŁĄD AKTUALIZACJI", this)
    }

    fun wrongDebetersFields() {
        showToast("Uzupełnij wymagane dane", this)

        id_name_debeter.setHintTextColor(Color.RED)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDepterToSave(debt: String, nameDebt: String, debtContact: LocalDate): Boolean {
        val incorect = incorrectDouble(debt)
        var debeter: Debeter = Debeter("", LocalDate.now(), 0.0)
        var debterIsCorrect = false
        if (incorect) showToast("Proszę wpisać liczbę w pole Dług", this)
        else if (isEmpty(nameDebt)) {
            showToast("Prosze uzupełnić pola Nazwa i Data", this)
        } else {
            debterIsCorrect = true
        }
        return debterIsCorrect
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save(debeter: Debeter) {
        if (checkDepterToSave(debeter.debt.toString(), debeter.name, debeter.data)) {
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

    private fun isEmpty(nameDebt: String): Boolean {
        return nameDebt.isNullOrEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickToSimulation(view: View) {
        val intent = Intent(this, SymulationActivity::class.java)
        val ir = id_name_debeter.text.toString()
        val debt = id_debeter_value.text.toString()
        if (ir.isNotEmpty() and debt.isNotEmpty()) {
            intent.putExtra("name", ir)
            intent.putExtra("value", debt)
            intent.putExtra("days", ChronoUnit.DAYS.between(debeter.data, LocalDate.now()).toInt())
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun whenPermissionGranted() {
        val date = LocalDate.parse(id_debeter_contact.text.toString())
        val debt = id_debeter_value.text.toString()

        val debterComplete = checkDepterToSave(
            debt,
            id_name_debeter.text.toString(),
            date
        )
        val obj = SmsManager.getDefault()
        if (debterComplete) {
            val date = date
            val debt = debt
            val textMessage = "Pożyczyłeś $date od właściciela tego numer $debt zł"
            obj.sendTextMessage(null, null, textMessage, null, null)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
