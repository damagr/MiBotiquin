package com.mibotiquin.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mibotiquin.MiBotiquinApplication
import com.mibotiquin.domain.usecase.DeleteProductUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ExpiryNotificationHelper.ACTION_DELETE_PRODUCT) return
        val productId = intent.getLongExtra(ExpiryNotificationHelper.EXTRA_PRODUCT_ID, -1L)
        if (productId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = MiBotiquinApplication.container(context).productRepository
                DeleteProductUseCase(repository)(productId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
