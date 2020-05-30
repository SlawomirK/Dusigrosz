package com.kobylinski.dusigrosz.helpers

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kobylinski.dusigrosz.R
import com.kobylinski.dusigrosz.model.Debeter
import kotlinx.android.synthetic.main.row_main.view.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MyVh(view: View) : RecyclerView.ViewHolder(view) {
    var name: TextView = view.id_name_debt
    var debt: TextView = view.id_Debt
    var data: TextView = view.id_data
    var days: TextView = view.id_dniPorzyczki


    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData(data: Debeter) {
        itemView.id_Debt.text = data.debt.toString() + "PLN"
        itemView.id_name_debt.text = data.name
        itemView.id_data.text = LocalDate.parse(data.data.toString()).toString()
        itemView.id_dniPorzyczki.text = getDaysOfLoan(data)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysOfLoan(data: Debeter) =
        "${ChronoUnit.DAYS.between(data.data, LocalDate.now())} dni"

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(debeter: Debeter, action: iOnDebtListener) {
        name.text = debeter.name
        debt.text = debeter.debt.toString() + " PLN"
        data.text = LocalDate.parse(debeter.data.toString().subSequence(0, 10)).toString()
        days.text = getDaysOfLoan(debeter)
        onShortClick(action, debeter)
        onLongClick(action, debeter)
    }

    private fun onLongClick(action: iOnDebtListener, debeter: Debeter) {
        itemView.setOnLongClickListener {
            action.onLongClick(debeter, adapterPosition)
            return@setOnLongClickListener true
        }
    }

    private fun onShortClick(action: iOnDebtListener, debeter: Debeter) {
        itemView.setOnClickListener {
            action.onClick(debeter, adapterPosition)
        }
    }

    interface iOnDebtListener {
        fun onClick(debt: Debeter, position: Int)
        fun onLongClick(debt: Debeter, position: Int)
    }
}

class MyAdapter1(var dataBase: List<Debeter>, var clickListener: MyVh.iOnDebtListener) :
    RecyclerView.Adapter<MyVh>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVh {
        return MyVh(LayoutInflater.from(parent.context).inflate(R.layout.row_main, parent, false))
    }

    override fun getItemCount(): Int = dataBase.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyVh, position: Int) {
        holder.refreshData(dataBase[position])
        holder.initialize(dataBase[position], clickListener)

    }
}