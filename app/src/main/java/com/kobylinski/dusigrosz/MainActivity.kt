package com.kobylinski.dusigrosz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.model.Debeter

class MainActivity : AppCompatActivity() {

    lateinit var dB: DatabaseHelper
    //Todo: na jutro content provider i udostępnianie sms
    companion object {
        var listDebters: List<Debeter> = ArrayList<Debeter>()
        fun getSumOfAllDebts(): Double {
            val sum= listDebters.sumByDouble { it.debt }
            return sum
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dB = DatabaseHelper(this)
        val d = dB.writableDatabase
        refreshData()
        createListView()
        sumAllDebts()
    }

    private fun refreshData() {
        listDebters = dB.getAllDebeters()
        val listDebt = findViewById<ListView>(R.id.listView_debeters)
        listDebt.adapter = MyAdapter(this);
        getSumOfAllDebts()
        sumAllDebts()
    }

    private fun errorStatement(st: String) {
        Toast.makeText(
            this,
            st,
            Toast.LENGTH_LONG
        ).show()
    }

    fun onClickToDebeter(view: View) {
        val intent = Intent(this, DluznikActivity::class.java)
        startActivity(intent)
    }

    private fun sumAllDebts() {
        val allDebts = findViewById<TextView>(R.id.text_sumDebts)
        allDebts.text = getSumOfAllDebts().toString() + " PLN"
    }

    private fun createListView() {
        val listDebt = findViewById<ListView>(R.id.listView_debeters)
        listDebt.adapter = MyAdapter(this);
    }


    inner class MyAdapter(context: Context) : BaseAdapter() {
        private val mContext: Context

        init {
            mContext = context
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_main, parent, false)
            val nameTextView = rowMain.findViewById<TextView>(R.id.id_name_debt)
            nameTextView.text = listDebters.get(position).name
            val debt = rowMain.findViewById<TextView>(R.id.id_Debt)
            debt.text = listDebters.get(position).debt.toString() + " PLN";
            rowSetOnClickListener(rowMain, position)
            rowSetOnLongClick(rowMain, position)
            return rowMain
        }

        private fun rowSetOnLongClick(rowMain: View?, position: Int) {
            if (rowMain != null) {
                rowMain.setOnLongClickListener() {
                    createRemoveDialog(position)
                    return@setOnLongClickListener true
                }
            }
        }

        private fun createRemoveDialog(position: Int) {
            val debeterId = dB.getId(listDebters.get(position))
            val debeter = listDebters.get(position)
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Usunięcie z listy dłużników")
            builder.setMessage("Czy na pewno chcesz usunąć " + debeter.name + "?")
            builder.setPositiveButton("USUŃ") { dialog, which ->
                run {
                    removeFromDB(debeterId, debeter)
                }
            }
            builder.setNeutralButton("Anuluj") { _, _ -> errorStatement("Anulowano") }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        private fun removeFromDB(debeterId: Long, debeter: Debeter) {
            val remove = dB.removeDebeter(debeterId.toString())
            errorStatement("Usunięto " + debeter.name)
            refreshData()
        }

        private fun rowSetOnClickListener(rowMain: View?, position: Int) {
            if (rowMain != null) {
                rowMain.setOnClickListener() {
                    val intent = addChoiceToIntent(position)
                    startActivity(intent)
                    refreshData()
                }
            }
        }

        private fun addChoiceToIntent(position: Int): Intent {
            val debt = listDebters.get(position)
            val intent = Intent(this@MainActivity, DluznikActivity::class.java)
            intent.putExtra("name", debt.name)
            intent.putExtra("phone", debt.phone)
            intent.putExtra("debt", debt.debt)
            return intent
        }

        override fun getItem(position: Int): Any {
            return listDebters.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listDebters.size
        }

    }
}


