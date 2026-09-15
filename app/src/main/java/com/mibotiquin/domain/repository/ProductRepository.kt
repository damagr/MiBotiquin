package com.mibotiquin.domain.repository

import com.mibotiquin.domain.model.Product
import com.mibotiquin.domain.model.ProductUiModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<ProductUiModel>>
    fun getProductsByCategory(category: String): Flow<List<ProductUiModel>>
    fun searchProducts(query: String): Flow<List<ProductUiModel>>
    suspend fun getProductById(id: Long): ProductUiModel?
    suspend fun getProductByBarcode(barcode: String): ProductUiModel?
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product): Int
    suspend fun updateQuantity(id: Long, quantity: Int): Int
    suspend fun updateExpiryDate(id: Long, expiryDate: Long): Int
    suspend fun deleteProduct(id: Long): Int
    suspend fun deleteProductByBarcode(barcode: String): Int
    suspend fun getEmptyCount(): Int
    suspend fun getExpiredCount(): Int
    suspend fun getExpiringSoonCount(): Int
}