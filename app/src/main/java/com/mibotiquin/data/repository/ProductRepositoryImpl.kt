package com.mibotiquin.data.repository

import com.mibotiquin.data.local.dao.ProductDao
import com.mibotiquin.data.local.mapper.toDomain
import com.mibotiquin.data.local.mapper.toEntity
import com.mibotiquin.domain.model.Product
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<ProductUiModel>> {
        return productDao.getAll().map { entities ->
            entities.map { it.toDomain().toUiModel() }
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<ProductUiModel>> {
        return productDao.getByCategory(category).map { entities ->
            entities.map { it.toDomain().toUiModel() }
        }
    }

    override fun searchProducts(query: String): Flow<List<ProductUiModel>> {
        if (query.isBlank()) return getAllProducts()
        return productDao.search(query.trim()).map { entities ->
            entities.map { it.toDomain().toUiModel() }
        }
    }

    override suspend fun getProductById(id: Long): ProductUiModel? {
        return productDao.getById(id).first()?.toDomain()?.toUiModel()
    }

    override suspend fun getProductByBarcode(barcode: String): ProductUiModel? {
        return productDao.getByBarcode(barcode).first()?.toDomain()?.toUiModel()
    }

    override suspend fun addProduct(product: Product): Long {
        return productDao.insert(product.toEntity())
    }

    override suspend fun updateProduct(product: Product): Int {
        return productDao.update(product.toEntity())
    }

    override suspend fun updateQuantity(id: Long, quantity: Int): Int {
        return productDao.getById(id).first()?.let { entity ->
            val updated = entity.copy(
                quantity = quantity,
                updatedAt = System.currentTimeMillis()
            )
            productDao.update(updated)
        } ?: 0
    }

    override suspend fun updateExpiryDate(id: Long, expiryDate: Long): Int {
        return productDao.getById(id).first()?.let { entity ->
            val updated = entity.copy(
                expiryDate = expiryDate,
                updatedAt = System.currentTimeMillis()
            )
            productDao.update(updated)
        } ?: 0
    }

    override suspend fun deleteProduct(id: Long): Int {
        return productDao.deleteById(id)
    }

    override suspend fun deleteProductByBarcode(barcode: String): Int {
        return productDao.deleteByBarcode(barcode)
    }

    override suspend fun getEmptyCount(): Int {
        return productDao.countEmpty()
    }

    override suspend fun getExpiredCount(): Int {
        return productDao.countExpired(System.currentTimeMillis())
    }

    override suspend fun getExpiringSoonCount(): Int {
        val now = System.currentTimeMillis()
        val thirtyDays = 30L * 24 * 60 * 60 * 1000
        return productDao.countExpiringSoon(now, now + thirtyDays)
    }
}

// Extension para convertir Product a ProductUiModel
private fun Product.toUiModel(): ProductUiModel {
    return ProductUiModel(product = this)
}