package com.mibotiquin.presentation.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mibotiquin.R
import com.mibotiquin.domain.model.Category
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.presentation.ui.components.ProductCard
import com.mibotiquin.presentation.ui.components.SearchBar

@Composable
fun HomeScreen(
    onOpenScanner: () -> Unit,
    scannedBarcode: String? = null,
    onBarcodeConsumed: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    // Permiso de notificaciones (API 33+) - una sola vez al abrir
    val context = androidx.compose.ui.platform.LocalContext.current
    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Search Bar — siempre visible, autofoco al abrir
        SearchBar(
            query = query,
            onQueryChange = viewModel::onSearchQueryChange,
            onScannerClick = onOpenScanner,
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth()
        )

        ProductList(
            products = products,
            query = query,
            onAddProduct = onOpenScanner,
            onQuantityChange = viewModel::onQuantityChange,
            onExpiryDateChange = viewModel::onExpiryDateChange,
            onDeleteProduct = viewModel::onDeleteProduct,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 100.dp)
        )
    }

    // Código escaneado → buscar si existe y abrir sheet (pre-rellenado si ya estaba)
    scannedBarcode?.let { barcode ->
        val existing by viewModel.existingForBarcode.collectAsStateWithLifecycle()

        LaunchedEffect(barcode) {
            viewModel.lookupBarcode(barcode)
        }

        com.mibotiquin.presentation.ui.components.AddProductSheet(
            barcode = barcode,
            existing = existing,
            onSave = { code, name, category, quantity, expiry ->
                viewModel.addProduct(code, name, category, quantity, expiry)
                onBarcodeConsumed()
            },
            onDismiss = {
                viewModel.clearLookup()
                onBarcodeConsumed()
            }
        )
    }
}

@Composable
private fun ProductList(
    products: List<ProductUiModel>,
    query: String,
    onAddProduct: () -> Unit,
    onQuantityChange: (ProductUiModel, Int) -> Unit,
    onExpiryDateChange: (ProductUiModel, Long) -> Unit,
    onDeleteProduct: (ProductUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) {
        EmptyState(query = query, onAddProduct = onAddProduct)
        return
    }

    val grouped = products.groupBy { it.product.category }
        .toList()
        .sortedBy { it.first.order }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 12.dp)
    ) {
        items(grouped) { (category, categoryProducts) ->
            CategorySection(
                category = category,
                products = categoryProducts,
                onQuantityChange = onQuantityChange,
                onExpiryDateChange = onExpiryDateChange,
                onDeleteProduct = onDeleteProduct
            )
        }
    }
}

@Composable
private fun CategorySection(
    category: Category,
    products: List<ProductUiModel>,
    onQuantityChange: (ProductUiModel, Int) -> Unit,
    onExpiryDateChange: (ProductUiModel, Long) -> Unit,
    onDeleteProduct: (ProductUiModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = category.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = products.size.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { product ->
                ProductCard(
                    product = product,
                    onQuantityChange = { onQuantityChange(product, it) },
                    onExpiryDateChange = { onExpiryDateChange(product, it) },
                    onDelete = { onDeleteProduct(product) }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    query: String,
    onAddProduct: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Medication,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = if (query.isBlank()) {
                    stringResource(R.string.empty_no_products)
                } else {
                    stringResource(R.string.empty_no_results, query)
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.empty_add_first),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            FilledTonalButton(
                onClick = onAddProduct,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Escanear código de barras", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun Category.icon() = when (this) {
    Category.MEDICINE -> Icons.Filled.Medication
    Category.FIRST_AID -> Icons.Filled.LocalHospital
    Category.TOPICAL -> Icons.Filled.Healing
}
