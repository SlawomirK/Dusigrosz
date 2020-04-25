package com.kobylinski.dusigrosz

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class CreateDialog() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    constructor (message: String) : this() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage("str")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}
