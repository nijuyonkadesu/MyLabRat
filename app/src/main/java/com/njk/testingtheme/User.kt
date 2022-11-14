package com.njk.testingtheme

data class User(
    val rfid: Int,
    val walletBalance: Int,
    val pendingPayment: Int,
    val ticketStatus: TicketStatus,
)
enum class TicketStatus {
    VALID, INVALID
}