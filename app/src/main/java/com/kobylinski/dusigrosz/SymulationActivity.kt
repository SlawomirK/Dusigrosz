package com.kobylinski.dusigrosz

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_symulation.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.properties.Delegates


class SymulationActivity : AppCompatActivity() {
    private lateinit var ti: MyCountDownTimer
    private var isTimerRunning: Boolean = false
    private var prowizja: BigDecimal= BigDecimal.ZERO

    private var pozostalyCzas: Long = 0
    private lateinit var pozostalaKwota: BigDecimal
    private val rata: Int by lazy { pozostalaKwota.toInt() / pozostalyCzas.toInt() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symulation)
        createWindow()

    }

    private fun createWindow() {
        val startSum = intent?.getStringExtra("value").toString().trim()//początkowa kwota
        if (checkCorrectness(startSum, "Niepoprawna kwota")) {
            pozostalaKwota = startSum.toBigDecimal()
        }
        val name = intent?.getStringExtra("name").toString()
        id_symul_text_who.text = "$name musi jeszcze oddać"
        id_symul_firstSum.text = "${pozostalaKwota.toInt()} zł"
        id_symul_text_interests.text = "Prowizja: $prowizja zł"

    }

    fun onClickSimulation(view: View) {
        val loanInterest = id_sym_prow.text.trim().toString()//odsetki
        val timeLoan = id_symul_timeLoan.text.trim().toString()//czas
        pozostalyCzas = timeLoan.toLong()
        if (checkCorrectness(loanInterest, "Pole odsetek niepoprawnie wypełnione")
            and checkCorrectness(timeLoan, "Pole okresu spłaty niepoprawnie wypełnione")
        ) {
            startSymulation(loanInterest, pozostalyCzas)
        }
    }
    private fun startSymulation(rate: String, time: Long) {
        if (!isTimerRunning) {
            ti=MyCountDownTimer(pozostalyCzas*1000,1000,rate.toBigDecimal())
            Log.wtf("pozostału czas",pozostalyCzas.toString())
            ti.start()
            isTimerRunning = true
            id_symul_button_symuluj.setText("Zatrzymaj")
        } else {
            ti.cancel()
            isTimerRunning = false
            id_symul_button_symuluj.setText("symuluj")
        }
    }

       inner class MyCountDownTimer(start: Long, interval: Long, val add: BigDecimal) :
        CountDownTimer(start, interval) {

        override fun onTick(infuture: Long) {
            prowizja += (pozostalaKwota.toDouble() * add.toInt() / 100).toBigDecimal()
            restOf()
            id_symul_text_interests.text = "Prowizja $prowizja"
            pozostalyCzas = infuture
        }

           private fun restOf() {
               pozostalaKwota -= rata.toBigDecimal()//odejmujeRate i aktualizuje kwotę do spłaty
               if (pozostalaKwota.toInt()>0) {
                   id_symul_firstSum.text = pozostalaKwota.toString()
               } else onFinish()
           }

           override fun onFinish() {
            id_symul_firstSum.setText("spłacone!")
        }
    }


    private fun checkCorrectness(field: String, alertText: String): Boolean {
        var bol = false
        if (isNotDouble(field)) CreateDialog(alertText) else bol = true
        return bol
    }

    private fun isNotDouble(st: String): Boolean {
        return !st.isEmpty().or(!st.toDouble().isNaN())
    }

}
