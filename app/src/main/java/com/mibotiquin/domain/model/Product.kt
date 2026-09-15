package com.mibotiquin.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class Product(
    val id: Long,
    val barcode: String,
    val name: String,
    val category: Category,
    val quantity: Int,
    val expiryDate: Long,  // epoch millis UTC
    val cabinetId: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    val daysUntilExpiry: Int
        get() {
            val expiry = Instant.ofEpochMilli(expiryDate).atZone(ZoneId.systemDefault()).toLocalDate()
            return ChronoUnit.DAYS.between(LocalDate.now(), expiry).toInt()
        }

    val expiryStatus: ExpiryStatus
        get() = when {
            quantity <= 0 -> ExpiryStatus.EMPTY
            daysUntilExpiry < 0 -> ExpiryStatus.EXPIRED
            daysUntilExpiry <= 7 -> ExpiryStatus.CRITICAL
            daysUntilExpiry <= 30 -> ExpiryStatus.SOON
            else -> ExpiryStatus.OK
        }
}

data class ProductUiModel(
    val product: Product,
    val expiryStatus: ExpiryStatus = product.expiryStatus,
    val daysUntilExpiry: Int = product.daysUntilExpiry
) {
    val formattedExpiryDate: String
        get() {
            val date = Instant.ofEpochMilli(product.expiryDate).atZone(ZoneId.systemDefault()).toLocalDate()
            return "%02d/%02d/%04d".format(date.dayOfMonth, date.monthValue, date.year)
        }

    val formattedDaysUntilExpiry: String
        get() = when {
            daysUntilExpiry < 0 -> "hace ${abs(daysUntilExpiry)} días"
            daysUntilExpiry == 0 -> "hoy"
            daysUntilExpiry == 1 -> "mañana"
            else -> "en $daysUntilExpiry días"
        }
}
