package com.kobylinski.dusigrosz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.kobylinski.dusigrosz.activity.DluznikActivity
import com.kobylinski.dusigrosz.database.DatabaseHelper.MyDataBase.openDatabase
import com.kobylinski.dusigrosz.helpers.CreateDialog.Companion.showToast
import com.kobylinski.dusigrosz.helpers.MyAdapter1
import com.kobylinski.dusigrosz.helpers.MyVh
import com.kobylinski.dusigrosz.model.Debeter
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author Sławomir Kobyliński
 * Pierwszy projekt zaliczeniowy z PRM na PJA
 *
 */
class MainActivity : AppCompatActivity(), MyVh.iOnDebtListener {


    private val dB by lazy {
        openDatabase(this)
    }

    companion object {

        lateinit var listDebters: List<Debeter>
        fun getSumOfAllDebts(): Double {
            return listDebters.sumByDouble { it.debt!! }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshData()
        createActionBar()
    }

    private fun createActionBar() {
        supportActionBar?.title = "Dusigrosz/Dłużnicy"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_account_balance_wallet_black_24dp)
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshData() {
        listDebters = dB.getAllDebeters()
        listView_debeters.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MyAdapter1(listDebters, this@MainActivity)
        }
        sumAllDebts()
    }


    fun onClickToDebeter(view: View) {
        val intent = Intent(this, DluznikActivity::class.java)
        startActivity(intent)
    }

    private fun sumAllDebts() {
        val allDebts = findViewById<TextView>(R.id.text_sumDebts)
        allDebts.text = getSumOfAllDebts().toString() + " PLN"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(debt: Debeter, position: Int) {
        val intent = addChoiceToIntent(position)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addChoiceToIntent(position: Int): Intent {
        val debt = listDebters[position]
        val intent = Intent(this@MainActivity, DluznikActivity::class.java)
        intent.putExtra("name", debt.name)
        intent.putExtra("date", debt.data.toString())
        intent.putExtra("debt", debt.debt)
        return intent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLongClick(debt: Debeter, position: Int) {
        createRemoveDialog(position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRemoveDialog(position: Int) {
        val debeterId = dB.getId(listDebters[position])
        val debeter = listDebters[position]
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Usunięcie z listy dłużników")
        builder.setMessage("Czy na pewno chcesz usunąć " + debeter.name + "?")
        builder.setPositiveButton("USUŃ") { dialog, which ->
            run {
                removeFromDB(debeterId, debeter)
                refreshData()
            }
        }
        builder.setNeutralButton("Anuluj") { _, _ -> showToast("Anulowano", this) }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun removeFromDB(debeterId: Long, debeter: Debeter) {
        dB.removeDebeter(debeterId.toString())
        showToast("Usunięto " + debeter.name, this)
        refreshData()
    }
}


