package com.example.practica1movil2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val prefManager = PreferenceManager(this)

        val etRegUser = findViewById<EditText>(R.id.etRegUser)
        val etRegPass = findViewById<EditText>(R.id.etRegPass)
        val btnSave = findViewById<Button>(R.id.btnSaveRegister)

        btnSave.setOnClickListener {
            val name = etRegUser.text.toString()
            val pass = etRegPass.text.toString()

            if (name.isNotEmpty() && pass.isNotEmpty()) {
                val newUser = User(name, pass)
                prefManager.saveUser(newUser) // GSON guarda el JSON aquí

                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra esta pantalla y vuelve al login
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}