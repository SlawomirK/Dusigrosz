package com.kobylinski.dusigrosz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DluznikActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dluznik)
    }
    fun onClickToSimulation(view:View){
        val intent= Intent(this,SymulationActivity::class.java)
        startActivity(intent)
    }
    fun onClickCancel(view: View){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
