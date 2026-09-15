package com.mibotiquin.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val updateProductQuantityUseCase: UpdateProductQuantityUseCase,
    private val updateProductExpiryDateUseCase: UpdateProductExpiryDateUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getEmptyCountUseCase: GetEmptyCountUseCase,
    private val getExpiredCountUseCase: GetExpiredCountUseCase,
    private val getExpiringSoonCountUseCase: GetExpiringSoonCountUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Reactivo: conmuta Flow según query (debounce ligero para no saturar Room)
    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<ProductUiModel>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) getProductsUseCase()
            else searchProductsUseCase(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val emptyCount: StateFlow<Int> = flow { emit(getEmptyCountUseCase()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val expiredCount: StateFlow<Int> = flow { emit(getExpiredCountUseCase()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val expiringSoonCount: StateFlow<Int> = flow { emit(getExpiringSoonCountUseCase()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query.trim()
    }

    fun onQuantityChange(product: ProductUiModel, newQuantity: Int) {
        viewModelScope.launch {
            updateProductQuantityUseCase(product.product.id, newQuantity.coerceAtLeast(0))
        }
    }

    fun onExpiryDateChange(product: ProductUiModel, newExpiryDate: Long) {
        viewModelScope.launch {
            updateProductExpiryDateUseCase(product.product.id, newExpiryDate)
        }
    }

    fun onDeleteProduct(product: ProductUiModel) {
        viewModelScope.launch {
            deleteProductUseCase(product.product.id)
        }
    }

    fun addProduct(barcode: String, name: String, category: com.mibotiquin.domain.model.Category, quantity: Int, expiryDate: Long) {
        viewModelScope.launch {
            addProductUseCase(
                com.mibotiquin.domain.model.Product(
                    id = 0, // Room autogenera
                    barcode = barcode,
                    name = name,
                    category = category,
                    quantity = quantity,
                    expiryDate = expiryDate,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
