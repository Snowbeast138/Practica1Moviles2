package com.example.practica1movil2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {
    private lateinit var prefManager: PreferenceManager
    private lateinit var container: LinearLayout
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Inicializar Managers y Vistas
        prefManager = PreferenceManager(this)
        container = findViewById(R.id.containerCartItems)
        tvTotal = findViewById(R.id.tvCartTotal)

        // Configurar flecha atrás en la barra
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mi Carrito"

        renderCart()

        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            if (prefManager.getCart().isNotEmpty()) {
                Toast.makeText(this, "Redirigiendo a facturación...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BillingActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        renderCart()
        // Cancelamos cualquier alarma pendiente si el usuario regresó al carrito
        cancelarAlarmaAbandono()
    }

    override fun onPause() {
        super.onPause()
        // Si el usuario sale de la pantalla y hay productos, programamos el recordatorio
        val cart = prefManager.getCart()
        if (cart.isNotEmpty()) {
            programarAlarmaAbandono(30) // 30 segundos para pruebas
        }
    }

    private fun renderCart() {
        container.removeAllViews()
        val cartItems = prefManager.getCart()
        var total = 0.0

        cartItems.forEachIndexed { index, product ->
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_product_card, container, false)

            val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
            val tvPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            val btnAction = itemView.findViewById<Button>(R.id.btnBuyItem)

            tvName.text = product.name
            tvPrice.text = "$${product.price}"

            // Personalizar botón para eliminar
            btnAction.text = "Eliminar"
            btnAction.setBackgroundColor(android.graphics.Color.RED)

            btnAction.setOnClickListener {
                eliminarDelCarrito(index)
            }

            total += product.price.toDoubleOrNull() ?: 0.0
            container.addView(itemView)
        }

        tvTotal.text = String.format("Total: $%.2f", total)
    }

    private fun eliminarDelCarrito(index: Int) {
        val cart = prefManager.getCart()
        if (index < cart.size) {
            cart.removeAt(index)
            prefManager.saveCart(cart)
            renderCart()
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun programarAlarmaAbandono(segundos: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AbandonReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1005,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = System.currentTimeMillis() + (segundos * 1000)

        // Usar setExact para asegurar puntualidad en dispositivos físicos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun cancelarAlarmaAbandono() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AbandonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1005, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}