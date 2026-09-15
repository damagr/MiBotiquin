package com.mibotiquin.data.backup

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.mibotiquin.domain.model.Category
import com.mibotiquin.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first

/**
 * Backup local en JSON (Fase 2).
 * Exporta/importa vía SAF (el usuario elige el fichero).
 */
class BackupManager(
    private val context: Context,
    private val repository: ProductRepository
) {
    private val gson = Gson()

    data class BackupProduct(
        val barcode: String,
        val name: String,
        val category: String,
        val quantity: Int,
        val expiryDate: Long
    )

    suspend fun export(uri: Uri): Result<Int> = runCatching {
        val products = repository.getAllProducts().first()
        val payload = products.map {
            BackupProduct(
                barcode = it.product.barcode,
                name = it.product.name,
                category = it.product.category.name,
                quantity = it.product.quantity,
                expiryDate = it.product.expiryDate
            )
        }
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(gson.toJson(payload).toByteArray(Charsets.UTF_8))
        } ?: error("No se pudo abrir el destino")
        payload.size
    }

    suspend fun import(uri: Uri): Result<Int> = runCatching {
        val json = context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        } ?: error("No se pudo leer el fichero")
        val items = gson.fromJson(json, Array<BackupProduct>::class.java).toList()
        items.forEach { item ->
            val existing = repository.getProductByBarcode(item.barcode)
            val product = com.mibotiquin.domain.model.Product(
                id = existing?.product?.id ?: 0,
                barcode = item.barcode,
                name = item.name,
                category = runCatching { Category.valueOf(item.category) }
                    .getOrDefault(Category.MEDICINE),
                quantity = item.quantity,
                expiryDate = item.expiryDate,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.addProduct(product)
        }
        items.size
    }
}
