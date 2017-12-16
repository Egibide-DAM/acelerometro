package com.jaureguialzo.pruebaacelerometro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    val IP = "192.168.1.103"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actualizarPantalla()

        // Desactivar la interacción con las barras
        seekBarH.setOnTouchListener() { v, m -> true }
        seekBarV.setOnTouchListener() { v, m -> true }

        servidor.text = " "

        botonIniciar.setOnClickListener(){

        }

        botonParar.setOnClickListener() {
            actualizarPantalla()

            seekBarH.progress = 50  // 0 -> 100
            seekBarV.progress = 50
        }

        enviar.setOnCheckedChangeListener(){ v, estado ->
            servidor.text = if ( estado == true ) "IP: ${IP}" else " "
        }

    }

    fun actualizarPantalla(x: Double? = null, y: Double? = null, z: Double? = null) {

        if (x != null) {
            this.labelX.text = "x: %.3f".format(x)  // REF: Formato: https://stackoverflow.com/a/33492636/5136913
        } else {
            this.labelX.text = "x"
        }

        if (y != null) {
            this.labelY.text = "y: %.3f".format(y)
        } else {
            this.labelY.text = "y"
        }

        if (z != null) {
            this.labelZ.text = "z: %.3f".format(z)
        } else {
            this.labelZ.text = "z"
        }

    }

}
