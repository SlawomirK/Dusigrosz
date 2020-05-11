package com.kobylinski.dusigrosz.helpers

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class CreateDialog() : AppCompatActivity() {

    constructor (message: String) : this() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    companion object {
        fun showToast(st: String, context: Context) {
            Toast.makeText(
                context,
                st,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
