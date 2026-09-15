package com.mibotiquin

import com.mibotiquin.domain.model.Category
import com.mibotiquin.domain.model.ExpiryStatus
import com.mibotiquin.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ExpiryLogicTest {

    private fun product(quantity: Int = 5, daysFromToday: Int): Product {
        val expiry = LocalDate.now().plusDays(daysFromToday.toLong())
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return Product(
            id = 1,
            barcode = "000",
            name = "Test",
            category = Category.MEDICINE,
            quantity = quantity,
            expiryDate = expiry,
            cabinetId = "test-cabinet",
            createdAt = 0,
            updatedAt = 0
        )
    }

    @Test
    fun `sin stock es EMPTY independientemente de la fecha`() {
        assertEquals(ExpiryStatus.EMPTY, product(quantity = 0, daysFromToday = 365).expiryStatus)
        assertEquals(ExpiryStatus.EMPTY, product(quantity = 0, daysFromToday = -10).expiryStatus)
    }

    @Test
    fun `a 31 dias o mas es OK`() {
        assertEquals(ExpiryStatus.OK, product(daysFromToday = 31).expiryStatus)
        assertEquals(ExpiryStatus.OK, product(daysFromToday = 365).expiryStatus)
    }

    @Test
    fun `entre 8 y 30 dias es SOON`() {
        assertEquals(ExpiryStatus.SOON, product(daysFromToday = 30).expiryStatus)
        assertEquals(ExpiryStatus.SOON, product(daysFromToday = 8).expiryStatus)
    }

    @Test
    fun `de 0 a 7 dias es CRITICAL`() {
        assertEquals(ExpiryStatus.CRITICAL, product(daysFromToday = 7).expiryStatus)
        assertEquals(ExpiryStatus.CRITICAL, product(daysFromToday = 0).expiryStatus)
    }

    @Test
    fun `caducado es EXPIRED`() {
        assertEquals(ExpiryStatus.EXPIRED, product(daysFromToday = -1).expiryStatus)
        assertEquals(ExpiryStatus.EXPIRED, product(daysFromToday = -100).expiryStatus)
    }

    @Test
    fun `daysUntilExpiry es correcto`() {
        assertEquals(5, product(daysFromToday = 5).daysUntilExpiry)
        assertEquals(-3, product(daysFromToday = -3).daysUntilExpiry)
    }
}
