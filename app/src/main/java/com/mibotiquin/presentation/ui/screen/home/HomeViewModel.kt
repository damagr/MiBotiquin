package com.mibotiquin.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mibotiquin.data.transfer.CabinetTransferManager
import com.mibotiquin.di.PreferencesManager
import com.mibotiquin.domain.model.Cabinet
import com.mibotiquin.domain.model.Category
import com.mibotiquin.domain.model.Product
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getCabinetsUseCase: GetCabinetsUseCase,
    private val createCabinetUseCase: CreateCabinetUseCase,
    private val deleteCabinetUseCase: DeleteCabinetUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val updateProductQuantityUseCase: UpdateProductQuantityUseCase,
    private val updateProductExpiryDateUseCase: UpdateProductExpiryDateUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase,
    private val transferManager: CabinetTransferManager,
    private val preferences: PreferencesManager
) : ViewModel() {

    // ---- Botiquines ----

    val cabinets: StateFlow<List<Cabinet>> = getCabinetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _activeCabinet = MutableStateFlow<Cabinet?>(null)
    val activeCabinet: StateFlow<Cabinet?> = _activeCabinet.asStateFlow()

    // UI: diálogo de creación / borrado / confirmación de importación
    private val _showCreateCabinet = MutableStateFlow(false)
    val showCreateCabinet: StateFlow<Boolean> = _showCreateCabinet.asStateFlow()

    private val _cabinetToDelete = MutableStateFlow<Cabinet?>(null)
    val cabinetToDelete: StateFlow<Cabinet?> = _cabinetToDelete.asStateFlow()

    private val _transferEvent = MutableStateFlow<String?>(null)
    val transferEvent: StateFlow<String?> = _transferEvent.asStateFlow()

    // Botiquín importado que colisiona con uno local más nuevo
    private val _hasNewerRemote = MutableStateFlow(false)
    val hasNewerRemote: StateFlow<Boolean> = _hasNewerRemote.asStateFlow()

    init {
        // Resolver botiquín activo al arrancar
        viewModelScope.launch {
            cabinets.collect { list ->
                val current = _activeCabinet.value
                val storedId = preferences.activeCabinetId
                when {
                    // Si no hay botiquines, activar el diálogo de creación
                    list.isEmpty() -> {
                        _activeCabinet.value = null
                        _showCreateCabinet.value = true
                    }
                    current?.id in list.map { it.id } -> Unit // ya activo
                    storedId != null && list.any { it.id == storedId } ->
                        _activeCabinet.value = list.first { it.id == storedId }
                    else -> {
                        _activeCabinet.value = list.first()
                        preferences.activeCabinetId = list.first().id
                    }
                }
            }
        }
    }

    fun selectCabinet(id: String) {
        val cabinet = cabinets.value.firstOrNull { it.id == id } ?: return
        _activeCabinet.value = cabinet
        preferences.activeCabinetId = cabinet.id
    }

    fun onShowCreateCabinet(show: Boolean) {
        _showCreateCabinet.value = show
    }

    fun createCabinet(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val cabinet = createCabinetUseCase(name.trim())
            _activeCabinet.value = cabinet
            preferences.activeCabinetId = cabinet.id
            _showCreateCabinet.value = false
        }
    }

    fun onRequestDeleteCabinet(cabinet: Cabinet) {
        _cabinetToDelete.value = cabinet
    }

    fun onDismissDeleteCabinet() {
        _cabinetToDelete.value = null
    }

    fun deleteCabinet(id: String) {
        viewModelScope.launch {
            deleteCabinetUseCase(id)
            // Si era el activo, el colector de init se encargará de activar otro
            if (cabinets.value.size <= 1) {
                preferences.activeCabinetId = null
                _showCreateCabinet.value = true
            }
            _cabinetToDelete.value = null
        }
    }

    fun shareCabinet(uri: android.net.Uri) {
        val cabinet = _activeCabinet.value ?: return
        viewModelScope.launch {
            val ok = transferManager.exportCabinet(cabinet.id, uri)
            _transferEvent.value = if (ok) "Botiquín exportado" else "Error al exportar"
        }
    }

    fun importCabinet(uri: android.net.Uri) {
        viewModelScope.launch {
            when (val result = transferManager.importCabinet(uri)) {
                is CabinetTransferManager.ImportResult.Success -> {
                    selectCabinet(result.cabinetId)
                    _transferEvent.value =
                        "Botiquín '${result.name}' importado (${result.productCount} productos)"
                }
                is CabinetTransferManager.ImportResult.RejectedOlderLocal -> {
                    _hasNewerRemote.value = true
                    _transferEvent.value = "Importación rechazada: tu versión local es más reciente"
                }
                is CabinetTransferManager.ImportResult.Error -> {
                    _transferEvent.value = result.message
                }
            }
        }
    }

    fun onTransferEventShown() {
        _transferEvent.value = null
        _hasNewerRemote.value = false
    }

    // ---- Búsqueda / productos ----

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val products: StateFlow<List<ProductUiModel>> =
        combine(_searchQuery, _activeCabinet) { query, cabinet -> query to cabinet }
            .flatMapLatest { (query, cabinet) ->
                when {
                    cabinet == null -> flowOf(emptyList())
                    query.isBlank() -> getProductsUseCase(cabinet.id)
                    else -> searchProductsUseCase(cabinet.id, query)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun addProduct(
        barcode: String,
        name: String,
        category: Category,
        quantity: Int,
        expiryDate: Long
    ) {
        val cabinet = _activeCabinet.value ?: return
        viewModelScope.launch {
            val existing = _existingForBarcode.value?.product
            addProductUseCase(
                Product(
                    id = existing?.id ?: 0,
                    barcode = barcode,
                    name = name,
                    category = category,
                    quantity = quantity,
                    expiryDate = expiryDate,
                    cabinetId = cabinet.id,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            _existingForBarcode.value = null
        }
    }

    // Re-escaneo: producto existente pre-rellena el sheet
    private val _existingForBarcode = MutableStateFlow<ProductUiModel?>(null)
    val existingForBarcode: StateFlow<ProductUiModel?> = _existingForBarcode

    fun lookupBarcode(barcode: String) {
        val cabinet = _activeCabinet.value ?: return
        viewModelScope.launch {
            _existingForBarcode.value = getProductByBarcodeUseCase(cabinet.id, barcode)
        }
    }

    fun clearLookup() {
        _existingForBarcode.value = null
    }
}
