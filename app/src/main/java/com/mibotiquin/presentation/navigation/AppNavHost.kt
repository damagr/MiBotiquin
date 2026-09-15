package com.mibotiquin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.mibotiquin.presentation.ui.screen.home.HomeScreen
import com.mibotiquin.presentation.ui.screen.home.HomeViewModel
import com.mibotiquin.presentation.ui.screen.scanner.ScannerScreen
import com.mibotiquin.presentation.ui.screen.scanner.ScannerViewModel

object AppDestinations {
    const val HOME = "home"
    const val SCANNER = "scanner"
    const val DEEP_LINK_SCAN_URI = "mibotiquin://scan"
    const val KEY_SCANNED_BARCODE = "scanned_barcode"
}

@Composable
fun AppNavHost(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AppDestinations.HOME) {

        composable(route = AppDestinations.HOME) { entry ->
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            val scannedBarcode by entry.savedStateHandle
                .getStateFlow<String?>(AppDestinations.KEY_SCANNED_BARCODE, null)
                .collectAsStateWithLifecycle()

            HomeScreen(
                onOpenScanner = { navController.navigate(AppDestinations.SCANNER) },
                scannedBarcode = scannedBarcode,
                onBarcodeConsumed = {
                    entry.savedStateHandle.remove<String>(AppDestinations.KEY_SCANNED_BARCODE)
                },
                viewModel = homeViewModel
            )
        }

        composable(
            route = AppDestinations.SCANNER,
            deepLinks = listOf(navDeepLink { uriPattern = AppDestinations.DEEP_LINK_SCAN_URI })
        ) {
            val scannerViewModel: ScannerViewModel = viewModel(factory = viewModelFactory)
            ScannerScreen(
                onScanComplete = { barcode ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(AppDestinations.KEY_SCANNED_BARCODE, barcode)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                viewModel = scannerViewModel
            )
        }
    }
}
