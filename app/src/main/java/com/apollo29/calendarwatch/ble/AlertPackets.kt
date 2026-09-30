package com.apollo29.calendarwatch.ble

/**
 * Alerts are sent to the watch in packets of [PACKET_SIZE] bytes: a 4 byte header
 * (0, year, month, day) followed by up to 4 alerts of 4 bytes each.
 */
object AlertPackets {

    const val PACKET_SIZE = 20

    /**
     * Splits the alerts into packets of [PACKET_SIZE] bytes, the last one padded with 0.
     */
    fun split(alerts: List<Byte>): List<ByteArray> {
        if (alerts.isEmpty()) return listOf(ByteArray(PACKET_SIZE))
        return alerts.chunked(PACKET_SIZE).map { chunk ->
            ByteArray(PACKET_SIZE).also { packet ->
                chunk.forEachIndexed { index, value -> packet[index] = value }
            }
        }
    }
}
