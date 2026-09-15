package com.mibotiquin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cabinets")
data class CabinetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
