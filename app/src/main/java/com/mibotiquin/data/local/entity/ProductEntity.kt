package com.mibotiquin.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mibotiquin.domain.model.Category

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["barcode", "cabinetId"], unique = true),
        Index(value = ["name"]),
        Index(value = ["category"]),
        Index(value = ["expiryDate"]),
        Index(value = ["cabinetId"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String,
    val name: String,
    val category: Category,
    val quantity: Int,
    val expiryDate: Long,          // epoch millis UTC
    val cabinetId: String,         // FK lógica hacia cabinets
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)