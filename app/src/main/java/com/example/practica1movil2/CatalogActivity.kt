package com.example.practica1movil2

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CatalogActivity : AppCompatActivity() {
    private val CHANNEL_ID = "COMPRAS_CHANNEL"
    private lateinit var prefManager: PreferenceManager
    private lateinit var containerProducts: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Inicializar Managers y Vistas
        prefManager = PreferenceManager(this)
        containerProducts = findViewById(R.id.containerProducts)

        // Configurar UI
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nuestra Tienda"

        createNotificationChannel()

        // Configurar Botón Flotante para añadir productos
        val fab = findViewById<FloatingActionButton>(R.id.fabAddProduct)
        fab.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    // onResume se ejecuta cada vez que regresamos de AddProductActivity
    override fun onResume() {
        super.onResume()
        renderProducts()
    }

    private fun renderProducts() {
        // Limpiar el contenedor para no duplicar vistas al recargar
        containerProducts.removeAllViews()

        val productList = prefManager.getProducts()

        if (productList.isEmpty()) {
            // Opcional: Podrías inflar una vista que diga "No hay productos"
            return
        }

        // Por cada producto guardado en GSON, creamos una tarjeta visual
        for (product in productList) {
            val productView = LayoutInflater.from(this).inflate(R.layout.item_product_card, containerProducts, false)

            val tvName = productView.findViewById<TextView>(R.id.tvItemName)
            val tvPrice = productView.findViewById<TextView>(R.id.tvItemPrice)
            val btnBuy = productView.findViewById<Button>(R.id.btnBuyItem)

            tvName.text = product.name
            tvPrice.text = "$${product.price} - ${product.category}"

            btnBuy.setOnClickListener {
                registrarVentaLocal(product.name)
                showPurchaseNotification(product.name)
            }

            containerProducts.addView(productView)
        }
    }

    private fun registrarVentaLocal(nombre: String) {
        Toast.makeText(this, "$nombre añadido al carrito", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showPurchaseNotification(producto: String) {
        val intent = Intent(this, BillingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("¡Compra Exitosa!")
            .setContentText("Compraste: $producto. ¿Deseas facturar?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Compras", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}