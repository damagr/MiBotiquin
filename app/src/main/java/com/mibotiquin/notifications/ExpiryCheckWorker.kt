package com.mibotiquin.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mibotiquin.MiBotiquinApplication
import com.mibotiquin.domain.model.ExpiryStatus
import com.mibotiquin.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.first

class ExpiryCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = MiBotiquinApplication.container(applicationContext).productRepository
            val products = GetProductsUseCase(repository)().first()
            products
                .filter {
                    it.expiryStatus == ExpiryStatus.SOON ||
                    it.expiryStatus == ExpiryStatus.CRITICAL ||
                    it.expiryStatus == ExpiryStatus.EXPIRED
                }
                .forEach { ExpiryNotificationHelper.notifyExpiry(applicationContext, it) }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "expiry_check_work"
    }
}
