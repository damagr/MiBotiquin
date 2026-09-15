package com.mibotiquin.presentation.ui.screen.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mibotiquin.domain.model.Product
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.domain.usecase.AddProductUseCase
import com.mibotiquin.domain.usecase.GetProductByBarcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _scannedProduct = MutableStateFlow<ProductUiModel?>(null)
    val scannedProduct: StateFlow<ProductUiModel?> = _scannedProduct

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            _scannedProduct.value = getProductByBarcodeUseCase(barcode)
        }
    }

    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            addProductUseCase(product)
        }
    }
}
