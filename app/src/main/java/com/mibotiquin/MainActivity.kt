package com.mibotiquin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mibotiquin.presentation.navigation.AppNavHost
import com.mibotiquin.presentation.ui.theme.MiBotiquinTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MiBotiquinTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModelFactory = MiBotiquinApplication.container(this).viewModelFactory)
                }
            }
        }
    }
}
