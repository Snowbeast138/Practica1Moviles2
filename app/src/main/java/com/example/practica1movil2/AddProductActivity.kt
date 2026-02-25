package com.example.practica1movil2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class AddProductActivity : AppCompatActivity() {
    private val CHANNEL_ID = "INVENTARIO_CHANNEL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Vender Producto"

        val etName = findViewById<EditText>(R.id.etProdName)
        val etPrice = findViewById<EditText>(R.id.etProdPrice)
        val etDesc = findViewById<EditText>(R.id.etProdDesc)
        val btnSave = findViewById<Button>(R.id.btnSaveProduct)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)

        // Configurar Spinner
        val categories = arrayOf("Electrónica", "Ropa", "Hogar", "Libros")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            if (name.isEmpty() || price.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                // 1. Guardar con Persistencia GSON
                val prefManager = PreferenceManager(this)
                val listaActual = prefManager.getProducts()
                listaActual.add(Product(name, price, desc, category))
                prefManager.saveProducts(listaActual)

                // 2. Lanzar Notificación de "Producto Agregado"
                showProductAddedNotification(name)

                Toast.makeText(this, "Producto publicado", Toast.LENGTH_SHORT).show()
                finish() // Regresa al catálogo
            }
        }
    }

    private fun showProductAddedNotification(productName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Inventario", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_add)
            .setContentTitle("Nuevo Producto en Venta")
            .setContentText("Se ha registrado correctamente: $productName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(202, builder.build())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}