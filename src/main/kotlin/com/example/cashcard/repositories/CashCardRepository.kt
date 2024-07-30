package com.example.cashcard.repositories

import com.example.cashcard.models.CashCard
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface CashCardRepository: CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long>
