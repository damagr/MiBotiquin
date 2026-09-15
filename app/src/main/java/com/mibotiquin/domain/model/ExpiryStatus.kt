package com.mibotiquin.domain.model

enum class ExpiryStatus {
    OK,           // Verde/sin highlight - más de 30 días
    SOON,         // Amarillo/borde accent - 8-30 días
    CRITICAL,     // Naranja/borde accent + bg - 1-7 días
    EXPIRED,      // Rojo/borde error - caducado (días < 0)
    EMPTY         // Rojo/borde error - cantidad = 0
}