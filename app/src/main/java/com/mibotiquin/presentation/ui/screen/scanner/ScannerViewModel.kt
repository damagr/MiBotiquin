package com.mibotiquin.presentation.ui.screen.scanner

import androidx.lifecycle.ViewModel
import com.mibotiquin.di.PreferencesManager

class ScannerViewModel(
    private val preferences: PreferencesManager
) : ViewModel() {

    val activeCabinetId: String?
        get() = preferences.activeCabinetId
}
