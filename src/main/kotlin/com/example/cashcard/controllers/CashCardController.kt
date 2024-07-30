package com.example.cashcard.controllers

import com.example.cashcard.models.CashCard
import com.example.cashcard.repositories.CashCardRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
@RequestMapping("/cashcards")
class CashCardController(
    private val cashCardRepository: CashCardRepository
) {
    @GetMapping("/{requestedId}")
    fun findById(@PathVariable requestedId: Long): ResponseEntity<CashCard> {
        val cashCardOptional = cashCardRepository.findById(requestedId)
        return if (cashCardOptional.isPresent) {
            ResponseEntity.ok(cashCardOptional.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    private fun createCashCard(
        @RequestBody newCashCardRequest: CashCard
    ): ResponseEntity<Void> {
        println(newCashCardRequest)
        val savedCashCard = cashCardRepository.save(newCashCardRequest)
        println(savedCashCard)
        val locationOfNewCashCard = URI.create("/cashcards/${savedCashCard.id}")
        return ResponseEntity.created(locationOfNewCashCard).build()
    }
}