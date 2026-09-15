package com.mibotiquin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
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
}

@Composable
fun AppNavHost(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AppDestinations.HOME) {
        composable(route = AppDestinations.HOME) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                onOpenScanner = { navController.navigate(AppDestinations.SCANNER) },
                viewModel = homeViewModel
            )
        }

        composable(
            route = AppDestinations.SCANNER,
            deepLinks = listOf(navDeepLink { uriPattern = AppDestinations.DEEP_LINK_SCAN_URI })
        ) {
            val scannerViewModel: ScannerViewModel = viewModel(factory = viewModelFactory)
            ScannerScreen(
                onScanComplete = { navController.popBackStack() },
                viewModel = scannerViewModel
            )
        }
    }
}
