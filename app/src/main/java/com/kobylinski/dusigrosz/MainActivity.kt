package com.kobylinski.dusigrosz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.kobylinski.dusigrosz.database.DatabaseHelper
import com.kobylinski.dusigrosz.model.Debeter

class MainActivity : AppCompatActivity() {

    internal lateinit var dB: DatabaseHelper

    companion object {
        var listDebters: List<Debeter> = ArrayList<Debeter>()
        fun getSumOfAllDebts(): Double {
            return listDebters.sumByDouble { it.debt }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dB = DatabaseHelper(this)
        val d=dB.writableDatabase

        refreshData()
        createListView()
        sumAllDebts()
    }

    private fun refreshData() {
        listDebters = dB.getAllDebeters()
        val listDebt = findViewById<ListView>(R.id.listView_debeters)
        listDebt.adapter = MyAdapter(this);
    }


    public fun onClickToDebeter(view: View) {
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

    private class MyAdapter(context: Context) : BaseAdapter() {
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
            return rowMain
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
