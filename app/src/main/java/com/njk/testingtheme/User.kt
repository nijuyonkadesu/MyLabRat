package com.njk.testingtheme

data class User(
    val rfid: Int,
    val walletBalance: Int,
    val pendingPayment: Int,
    val ticketStatus: TicketStatus,
//    val tokenFcm: String,
)
enum class TicketStatus {
    VALID, INVALID
}