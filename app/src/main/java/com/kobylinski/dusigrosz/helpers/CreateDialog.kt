package com.kobylinski.dusigrosz.helpers

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity

class CreateDialog() : AppCompatActivity() {

    constructor (message: String) : this() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage("str")
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}
