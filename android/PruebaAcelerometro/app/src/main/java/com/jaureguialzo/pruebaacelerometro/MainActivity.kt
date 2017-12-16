package com.jaureguialzo.pruebaacelerometro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    val IP = "192.168.1.103"

    // REF: Uso de sensores: https://developer.android.com/guide/topics/sensors/sensors_motion.html
    var mSensorManager: SensorManager? = null
    var mSensor: Sensor? = null

    private val TAG = "MainActivity"

    var n = 0

    // REF: Biblioteca de red Volley: https://developer.android.com/training/volley/index.html
    var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actualizarPantalla()

        // Desactivar la interacción con las barras
        seekBarH.setOnTouchListener() { v, m -> true }
        seekBarV.setOnTouchListener() { v, m -> true }

        servidor.text = " "

        queue = Volley.newRequestQueue(this)

        // Evento del boton Iniciar
        botonIniciar.setOnClickListener() {
            mSensorManager!!.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        // Evento del boton Parar
        botonParar.setOnClickListener() {
            mSensorManager!!.unregisterListener(this);

            actualizarPantalla()

            seekBarH.progress = 50  // 0 -> 100
            seekBarV.progress = 50

            n = 0
        }

        // Mostrar la dirección del servidor si se activa el check
        enviar.setOnCheckedChangeListener() { v, estado ->
            servidor.text = if (estado == true) "IP: ${IP}" else " "
        }

        // Inicializar el gestor de sensores
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    override fun onPause() {
        super.onPause()

        // REF: No se para solo si cerramos la actividad: https://developer.android.com/reference/android/hardware/SensorManager.html
        mSensorManager!!.unregisterListener(this);
    }

    // REF: Recibir eventos: https://developer.android.com/reference/android/hardware/SensorEventListener.html
    override fun onAccuracyChanged(event: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val x = event!!.values[0];
        val y = event!!.values[1];
        val z = event!!.values[2];

        actualizarPantalla(x, y, z)

        // Convertir el valor del acelerómetro (-9.81 -> 9.81 ) al rango de las barras (0 -> 100)
        val g = 9.81

        val X = ((x / g * 50) + 50).toInt()
        val Y = ((y / g * 50) + 50).toInt()

        seekBarH.progress = X
        seekBarV.progress = Y

        // Enviar los datos al servidor mediante Volley
        if (enviar.isChecked) {

            // REF: Añadir parámetros: https://mobikul.com/parameters-to-volley/
            var url = "http://${IP}:3000/"

            url += "?android=${n}"
            url += "&x=${x}"
            url += "&y=${y}"
            url += "&z=${z}"

            // Generar la petición y crear los listeners para ver qué ha pasado
            val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> {
                        Log.d(TAG, "Enviado: ${n}")
                    },
                    Response.ErrorListener {
                        Log.e(TAG, "Error de envío: ${n}")
                    }
            )

            // Encolar la petición HTTP
            queue!!.add(stringRequest)

            n += 1
        }

    }

    fun actualizarPantalla(x: Float? = null, y: Float? = null, z: Float? = null) {

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
