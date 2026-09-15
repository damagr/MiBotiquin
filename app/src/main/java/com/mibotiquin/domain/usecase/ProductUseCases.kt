package com.mibotiquin.domain.usecase

import com.mibotiquin.domain.model.Product
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<List<ProductUiModel>> = repository.getAllProducts()
}

class GetProductsByCategoryUseCase(private val repository: ProductRepository) {
    operator fun invoke(category: String): Flow<List<ProductUiModel>> = repository.getProductsByCategory(category)
}

class SearchProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(query: String): Flow<List<ProductUiModel>> = repository.searchProducts(query)
}

class GetProductByIdUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): ProductUiModel? = repository.getProductById(id)
}

class GetProductByBarcodeUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(barcode: String): ProductUiModel? = repository.getProductByBarcode(barcode)
}

class AddProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product): Long = repository.addProduct(product)
}

class UpdateProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product): Int = repository.updateProduct(product)
}

class UpdateProductQuantityUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long, quantity: Int): Int = repository.updateQuantity(id, quantity)
}

class UpdateProductExpiryDateUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long, expiryDate: Long): Int = repository.updateExpiryDate(id, expiryDate)
}

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): Int = repository.deleteProduct(id)
}

class DeleteProductByBarcodeUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(barcode: String): Int = repository.deleteProductByBarcode(barcode)
}

class GetEmptyCountUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Int = repository.getEmptyCount()
}

class GetExpiredCountUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Int = repository.getExpiredCount()
}

class GetExpiringSoonCountUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Int = repository.getExpiringSoonCount()
}
