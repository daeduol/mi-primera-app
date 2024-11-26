package com.example.myapplication


data class Pregunta(val pregunta: String, val opciones: List<String>, val correcta: Int)
data class PreguntasResponse(val preguntas: List<Pregunta>)

