package com.mibotiquin.data.local.mapper

import com.mibotiquin.data.local.entity.ProductEntity
import com.mibotiquin.domain.model.Product

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    barcode = barcode,
    name = name,
    category = category,
    quantity = quantity,
    expiryDate = expiryDate,
    cabinetId = cabinetId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    barcode = barcode,
    name = name,
    category = category,
    quantity = quantity,
    expiryDate = expiryDate,
    cabinetId = cabinetId,
    createdAt = createdAt,
    updatedAt = updatedAt
)
