package com.mibotiquin

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mibotiquin.data.local.database.AppDatabase
import com.mibotiquin.data.repository.ProductRepositoryImpl
import com.mibotiquin.domain.usecase.*
import com.mibotiquin.presentation.ui.screen.home.HomeViewModel
import com.mibotiquin.presentation.ui.screen.scanner.ScannerViewModel

class MiBotiquinApplication : Application() {

    val diContainer by lazy { DiContainer(this) }

    companion object {
        fun container(context: Context): DiContainer =
            (context.applicationContext as MiBotiquinApplication).diContainer
    }
}

class DiContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val productRepository by lazy { ProductRepositoryImpl(database.productDao()) }

    val viewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(
                getProductsUseCase = GetProductsUseCase(productRepository),
                searchProductsUseCase = SearchProductsUseCase(productRepository),
                updateProductQuantityUseCase = UpdateProductQuantityUseCase(productRepository),
                updateProductExpiryDateUseCase = UpdateProductExpiryDateUseCase(productRepository),
                deleteProductUseCase = DeleteProductUseCase(productRepository),
                getEmptyCountUseCase = GetEmptyCountUseCase(productRepository),
                getExpiredCountUseCase = GetExpiredCountUseCase(productRepository),
                getExpiringSoonCountUseCase = GetExpiringSoonCountUseCase(productRepository),
                addProductUseCase = AddProductUseCase(productRepository)
            ) as T

            ScannerViewModel::class.java -> ScannerViewModel(
                getProductByBarcodeUseCase = GetProductByBarcodeUseCase(productRepository),
                addProductUseCase = AddProductUseCase(productRepository)
            ) as T

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
