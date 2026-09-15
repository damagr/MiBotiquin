package com.mibotiquin.data.local.mapper

import com.mibotiquin.data.local.entity.ProductEntity
import com.mibotiquin.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        barcode = barcode,
        name = name,
        category = category,
        quantity = quantity,
        expiryDate = expiryDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        barcode = barcode,
        name = name,
        category = category,
        quantity = quantity,
        expiryDate = expiryDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<ProductEntity>.toDomainList(): List<Product> {
    return map { it.toDomain() }
}