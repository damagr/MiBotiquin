package com.mibotiquin.domain.model

enum class Category(
    val displayName: String,
    val order: Int
) {
    MEDICINE("Medicamentos", 1),
    FIRST_AID("Primeros auxilios", 2),
    TOPICAL("Tratamientos tópicos", 3)
}