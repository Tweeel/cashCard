package com.example.cashcard.models

import org.springframework.data.annotation.Id

data class CashCard(
    @Id
    val id: Long?,
    val amount: Double
)
