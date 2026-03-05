package com.example.practica1movil2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PromoValidationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_validation)

        val prefManager = PreferenceManager(this)
        val cart = prefManager.getCart()

        // Calcular total original
        var total = 0.0
        cart.forEach { total += it.price.toDoubleOrNull() ?: 0.0 }

        val tvTotalInfo = findViewById<TextView>(R.id.tvTotalInfo)
        tvTotalInfo.text = "Total actual en carrito: $$total"

        val etCode = findViewById<EditText>(R.id.etPromoCode)
        val btnValidate = findViewById<Button>(R.id.btnValidatePromo)

        btnValidate.setOnClickListener {
            if (etCode.text.toString().trim() == "OFERTA20") {
                val cart = prefManager.getCart()

                // Aplicar el descuento a cada producto de la lista
                val cartConDescuento = cart.map { producto ->
                    val precioOriginal = producto.price.toDoubleOrNull() ?: 0.0
                    val nuevoPrecio = precioOriginal * 0.80
                    // Creamos una copia del producto con el nuevo precio formateado
                    producto.copy(price = String.format("%.2f", nuevoPrecio))
                }

                // Guardamos la lista actualizada en GSON
                prefManager.saveCart(cartConDescuento)

                val nuevoTotal = cartConDescuento.sumOf { it.price.toDouble() }

                Toast.makeText(this, "¡Descuento aplicado! Total actualizado: $$nuevoTotal", Toast.LENGTH_LONG).show()

                // Regresamos al catálogo o carrito; ahora al abrir el carrito verás los precios bajos
                finish()
            } else {
                etCode.error = "Código incorrecto"
            }
        }
    }
}