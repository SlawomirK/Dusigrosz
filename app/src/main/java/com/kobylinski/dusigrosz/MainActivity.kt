package com.kobylinski.dusigrosz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.kobylinski.dusigrosz.model.Debeter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createListView()
        sumAllDebts()

    }

    public fun onClickToDebeter(view:View){
        val intent=Intent(this,DluznikActivity::class.java)
        startActivity(intent)
    }

    private fun sumAllDebts() {
        val allDebts=findViewById<TextView>(R.id.text_sumDebts)
        allDebts.text=Debeter.getAllDebts().toString()+" PLN"
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
            nameTextView.text = Debeter.names.get(position).name
            val debt = rowMain.findViewById<TextView>(R.id.id_Debt)
            debt.text =  Debeter.names.get(position).debt.toString() + " PLN";
            return rowMain
        }

        override fun getItem(position: Int): Any {
            return Debeter.names.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return Debeter.names.size
        }

    }
}
