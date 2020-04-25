package com.kobylinski.dusigrosz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_symulation.*


class SymulationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symulation)
        createWindow()

    }

    private fun createWindow() {
        val loan = intent?.getStringExtra("value").toString()
        val name = intent?.getStringExtra("name").toString()
        sum = loan.toDouble()
        id_symul_text_who.setText(name + " chce pożyczyć: " + loan + " zł")
        id_symul_text_interests.setText("Po uwzlędnieniu odsetek powinien oddać: $loan zł")
    }

    private var sum = 0.0
    fun onClickSimulation(view: View) {
        val loanInterest = id_sym_prow.text.trim().toString()
        val timeLoan = id_symul_timeLoan.text.trim().toString()
        if (checkCorrectness(
                loanInterest, "Pole odsetek niepoprawnie wypełnione"
            ) and checkCorrectness(timeLoan, "Pole okresu spłaty niepoprawnie wypełnione")
        ) {
            startSymulation(loanInterest, timeLoan)
        }
    }

    private fun startSymulation(loanInterest: String, timeLoan: String) {
        val li = loanInterest.toInt()
        val tl = timeLoan.toInt()

    }

    private fun checkCorrectness(field: String, alertText: String): Boolean {
        var bol = false
        if (isDouble(field)) CreateDialog(alertText) else bol = true
        return bol
    }

    private fun isDouble(st: String): Boolean {
        return !st.isEmpty().and(!st?.toDouble().isNaN())
    }

}
