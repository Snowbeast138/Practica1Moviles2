package com.example.practica1movil2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class BillingActivity : AppCompatActivity() {
    private val CHANNEL_ID = "FACTURACION_CHANNEL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Generar Factura"

        val etRfc = findViewById<EditText>(R.id.etRFC)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val btnFinish = findViewById<Button>(R.id.btnFinishBilling)

        btnFinish.setOnClickListener {
            val rfc = etRfc.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (rfc.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Error: Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else if (rfc.length < 10) {
                etRfc.error = "RFC muy corto"
            } else {
                // Enviar notificación de éxito con datos
                showInvoiceSuccessNotification(rfc, address)

                Toast.makeText(this, "Factura procesada", Toast.LENGTH_SHORT).show()
                finish() // Ahora sí regresará al Catálogo porque no matamos la tarea
            }
        }
    }

    private fun showInvoiceSuccessNotification(rfc: String, address: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Facturación", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("Factura Generada Correctamente")
            .setContentText("RFC: $rfc | Dir: $address")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Se ha emitido la factura para el RFC: $rfc con domicilio en: $address"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(303, builder.build())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}