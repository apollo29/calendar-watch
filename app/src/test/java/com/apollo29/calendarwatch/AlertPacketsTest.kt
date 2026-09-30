package com.apollo29.calendarwatch

import com.apollo29.calendarwatch.ble.AlertPackets
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class AlertPacketsTest {

    private val header = listOf<Byte>(0, 26, 9, 30)

    private fun alert(n: Int) = listOf<Byte>(0, n.toByte(), 1, 2)

    @Test
    fun noAlerts_sendsOneEmptyPacket() {
        val packets = AlertPackets.split(emptyList())
        assertEquals(1, packets.size)
        assertArrayEquals(ByteArray(20), packets[0])
    }

    @Test
    fun headerOnly_isPadded() {
        val packets = AlertPackets.split(header)
        assertEquals(1, packets.size)
        assertArrayEquals((header + List(16) { 0.toByte() }).toByteArray(), packets[0])
    }

    @Test
    fun fourAlerts_fitInOnePacket() {
        val alerts = header + alert(1) + alert(2) + alert(3) + alert(4)
        val packets = AlertPackets.split(alerts)
        assertEquals(1, packets.size)
        assertArrayEquals(alerts.toByteArray(), packets[0])
    }

    @Test
    fun fifthAlert_startsNewPacketWithHeader() {
        val first = header + alert(1) + alert(2) + alert(3) + alert(4)
        val second = header + alert(5)
        val packets = AlertPackets.split(first + second)
        assertEquals(2, packets.size)
        assertArrayEquals(first.toByteArray(), packets[0])
        assertArrayEquals((second + List(12) { 0.toByte() }).toByteArray(), packets[1])
    }
}
