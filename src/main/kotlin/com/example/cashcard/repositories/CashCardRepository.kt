package com.example.cashcard.repositories

import com.example.cashcard.models.CashCard
import org.springframework.data.repository.CrudRepository

interface CashCardRepository: CrudRepository<CashCard, Long>
