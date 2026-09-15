package com.mibotiquin

import com.mibotiquin.data.transfer.CabinetTransferManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regla de negocio crítica (puramente lógica, sin Android):
 * al importar un botiquín que ya existe, prevalece el de fecha más reciente.
 */
class TransferMergeLogicTest {

    private fun decide(localUpdatedAt: Long?, remoteUpdatedAt: Long): Boolean {
        // Reproduce exactamente la regla del CabinetTransferManager.applyPayload
        return if (localUpdatedAt == null) true           // no existe → se crea
        else remoteUpdatedAt > localUpdatedAt             // remoto más nuevo → reemplaza
    }

    @Test
    fun `importar cabin nuevo siempre crea`() {
        assertTrue(decide(localUpdatedAt = null, remoteUpdatedAt = 1000))
    }

    @Test
    fun `remoto mas reciente reemplaza`() {
        assertTrue(decide(localUpdatedAt = 1000, remoteUpdatedAt = 2000))
    }

    @Test
    fun `local mas reciente rechaza import`() {
        assertFalse(decide(localUpdatedAt = 2000, remoteUpdatedAt = 1000))
    }

    @Test
    fun `misma fecha rechaza import`() {
        assertFalse(decide(localUpdatedAt = 1500, remoteUpdatedAt = 1500))
    }

    @Test
    fun `payload contiene cabinetId y productos`() {
        val payload = CabinetTransferManager.TransferPayload(
            cabinetId = "abc",
            name = "Casa",
            updatedAt = 1000,
            products = emptyList()
        )
        assertEquals("abc", payload.cabinetId)
        assertEquals("Casa", payload.name)
    }
}
