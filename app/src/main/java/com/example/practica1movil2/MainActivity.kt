package com.example.practica1movil2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Código de solicitud para identificar la respuesta del usuario
    private val NOTIFICATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Verificar y pedir permisos al iniciar la app
        checkNotificationPermission()

        val prefManager = PreferenceManager(this)
        val etUser = findViewById<EditText>(R.id.etUser)
        val etPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val inputUser = etUser.text.toString()
            val inputPass = etPass.text.toString()
            val savedUser = prefManager.getUser()

            if (savedUser != null && savedUser.username == inputUser && savedUser.password == inputPass) {
                Toast.makeText(this, "¡Bienvenido, ${savedUser.username}!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CatalogActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkNotificationPermission() {
        // El permiso solo es necesario en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Si no tenemos el permiso, lo pedimos
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    // Manejar la respuesta del usuario al cuadro de diálogo de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Las notificaciones están desactivadas. No verás tus confirmaciones de compra.", Toast.LENGTH_LONG).show()
            }
        }
    }
}