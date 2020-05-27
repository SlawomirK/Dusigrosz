package com.kobylinski.dusigrosz.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.kobylinski.dusigrosz.R
import com.kobylinski.dusigrosz.helpers.CreateDialog
import kotlinx.android.synthetic.main.activity_symulation.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*


class SymulationActivity : AppCompatActivity() {
    private lateinit var ti: MyCountDownTimer
    private var isTimerRunning: Boolean = false
    private var prowizja: BigDecimal = BigDecimal.ZERO
    private var pozostalyCzas: Long = 0
    private lateinit var pozostalaKwota: BigDecimal
    private val rata: Int by lazy { pozostalaKwota.toInt() / pozostalyCzas.toInt() }
    private var start = BigDecimal.ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symulation)
        start = intent?.getStringExtra("value").toString().trim().toBigDecimal()
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
        id_symul_timeLoan.setText(intent?.getIntExtra("days", -1).toString())

    }

    @SuppressLint("SetTextI18n")
    fun onClickSimulation(view: View) {
        val loanInterest = id_sym_prow.text.trim().toString()//odsetki
        val timeLoan = id_symul_timeLoan.text.trim().toString()//czas
        pozostalyCzas = timeLoan.toLong()
        if (checkCorrectness(loanInterest, "Pole odsetek niepoprawnie wypełnione")
            and checkCorrectness(timeLoan, "Pole okresu spłaty niepoprawnie wypełnione")
        ) {
            //  startSymulation(loanInterest, pozostalyCzas)
            val dzienne = (start.toDouble() * (loanInterest.toDouble() / 100) / 365)
            prowizja = (dzienne * timeLoan.toDouble()).toBigDecimal()
        }
        val locale = Locale("pl", "PL")
        id_symul_text_interests.text =
            "Prowizja ${NumberFormat.getCurrencyInstance(locale).format(prowizja)}"
    }

    private fun startSymulation(rate: String, time: Long) {
        //w projekcie interval byl 1000 dla siebie nie potrzebuje przerw ale gotowy wynik bez czekania
        if (!isTimerRunning) {
            ti = MyCountDownTimer(pozostalyCzas, 10, rate.toBigDecimal())
            ti.start()
            isTimerRunning = true
            id_symul_button_symuluj.text = "Zatrzymaj"
        } else {
            if (id_symul_firstSum.text != "spłacone!") {
                ti.cancel()
                isTimerRunning = false
                id_symul_button_symuluj.text = "symuluj"
            }
        }
    }

    inner class MyCountDownTimer(start: Long, interval: Long, private val add: BigDecimal) :
        CountDownTimer(start, interval) {

        override fun onTick(infuture: Long) {//prywatnie i tak oddaje się od razu całą sumę lub w sposób
            //nieregularny więc odsetki będą naliczane dziennie od całej sumy, niepomniejszane o raty
            prowizja += ((pozostalaKwota.toDouble() * add.toInt() / 100)).toBigDecimal()//
//ustalę odsetki tak jak się liczy zazwyczaj na sztywno

            start += (start.toDouble() * add.toInt() / 100).toBigDecimal()
            restOf()
            id_symul_text_interests.text = "Prowizja ${prowizja.toInt()} PLN"
            pozostalyCzas = infuture
        }

        private fun restOf() {
            pozostalaKwota -= rata.toBigDecimal()//odejmujeRate i aktualizuje kwotę do spłaty
            if (pozostalaKwota.toInt() > 0) {
                id_symul_firstSum.text = pozostalaKwota.toString()
            } else onFinish()
        }

        override fun onFinish() {
            ti.cancel()
            isTimerRunning = false
            id_symul_firstSum.text = "spłacone!"
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
