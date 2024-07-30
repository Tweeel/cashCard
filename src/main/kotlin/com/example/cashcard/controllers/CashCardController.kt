package com.example.cashcard.controllers

import com.example.cashcard.models.CashCard
import com.example.cashcard.repositories.CashCardRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cashcards")
class CashCardController(
    private val cashCardRepository: CashCardRepository
) {
    @GetMapping("/{requestedId}")
    fun findById(@PathVariable requestedId: Long): ResponseEntity<CashCard> {
        val cashCardOptional = cashCardRepository.findById(requestedId)
        return if (cashCardOptional.isPresent) {
            ResponseEntity.ok(cashCardOptional.get());
        } else {
            ResponseEntity.notFound().build();
        }
    }
}