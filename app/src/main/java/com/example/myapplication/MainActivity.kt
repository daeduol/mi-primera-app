package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.IOException

class MainActivity : AppCompatActivity() {
    data class Pregunta(val pregunta: String, val opciones: List<String>, val correcta: Int)
    data class PreguntasResponse(val preguntas: List<Pregunta>)

    private lateinit var textoPregunta: TextView
    private lateinit var opcion1: Button
    private lateinit var opcion2: Button
    private lateinit var opcion3: Button
    private lateinit var opcion4: Button
    private lateinit var textoFeedback: TextView  // Para mostrar feedback

    private var preguntas: List<Pregunta> = listOf()
    private var indicePreguntaActual = 0
    private var puntaje = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoPregunta = findViewById(R.id.textoPregunta)
        opcion1 = findViewById(R.id.opcion1)
        opcion2 = findViewById(R.id.opcion2)
        opcion3 = findViewById(R.id.opcion3)
        opcion4 = findViewById(R.id.opcion4)
        textoFeedback = findViewById(R.id.textoFeedback)  // Inicializa el TextView para feedback

        cargarPreguntas()
        mostrarPregunta()

        val botones = listOf(opcion1, opcion2, opcion3, opcion4)
        botones.forEachIndexed { index, button ->
            button.setOnClickListener { verificarRespuesta(index) }
        }
    }

    private fun cargarPreguntas() {
        try {
            val json = leerArchivoJSON("preguntas.json")
            if (json != null) {
                val response = Gson().fromJson(json, PreguntasResponse::class.java)
                preguntas = response.preguntas.shuffled()
                Log.d("Preguntas", "Preguntas cargadas: $preguntas")
            } else {
                textoPregunta.text = "Error al cargar preguntas."
            }
        } catch (e: Exception) {
            textoPregunta.text = "Se produjo un error: ${e.message}"
            e.printStackTrace()
        }
    }

    private fun leerArchivoJSON(nombreArchivo: String): String? {
        return try {
            val inputStream = assets.open(nombreArchivo)
            val tamaño = inputStream.available()
            val buffer = ByteArray(tamaño)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun mostrarPregunta() {
        if (indicePreguntaActual < preguntas.size) {
            val pregunta = preguntas[indicePreguntaActual]
            textoPregunta.text = pregunta.pregunta
            opcion1.text = pregunta.opciones[0]
            opcion2.text = pregunta.opciones[1]
            opcion3.text = pregunta.opciones[2]
            opcion4.text = pregunta.opciones[3]
        } else {
            textoPregunta.text = "Juego terminado. Tu puntaje es: $puntaje/${preguntas.size}"
        }
    }

    private fun verificarRespuesta(opcionSeleccionada: Int) {
        val pregunta = preguntas[indicePreguntaActual]
        if (opcionSeleccionada == pregunta.correcta) {
            puntaje++
            textoFeedback.text = "¡Correcto!"
        } else {
            textoFeedback.text = "Incorrecto. La respuesta correcta era: ${pregunta.opciones[pregunta.correcta]}"
        }
        textoFeedback.visibility = View.VISIBLE  // Muestra el feedback
        indicePreguntaActual++

        // Espera un momento antes de mostrar la siguiente pregunta
        Handler().postDelayed({
            mostrarPregunta()
            textoFeedback.visibility = View.GONE  // Oculta el feedback para la siguiente pregunta
        }, 2000) // Espera 2 segundos antes de avanzar
    }
}
