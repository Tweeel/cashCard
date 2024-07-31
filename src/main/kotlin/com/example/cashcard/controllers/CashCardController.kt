package com.example.cashcard.controllers

import com.example.cashcard.models.CashCard
import com.example.cashcard.repositories.CashCardRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal


@RestController
@RequestMapping("/cashcards")
class CashCardController(
    private val cashCardRepository: CashCardRepository
) {

    private fun findCashCard(requestedId: Long, principal: Principal): CashCard? {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.name)
    }

    @GetMapping
    private fun findAll(pageable: Pageable, principal: Principal): ResponseEntity<List<CashCard>> {
        val page = cashCardRepository.findByOwner(
            principal.name,
            PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            )
        )
        return ResponseEntity.ok(page.content)
    }

    @GetMapping("/{requestedId}")
    fun findById(@PathVariable requestedId: Long, principal: Principal): ResponseEntity<CashCard> {
        val cashCard = findCashCard(requestedId, principal)
        return cashCard?.let {
            ResponseEntity.ok(cashCard)
        } ?: run {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    private fun createCashCard(
        @RequestBody newCashCardRequest: CashCard,
        principal: Principal
    ): ResponseEntity<Void> {
        val savedCashCard = cashCardRepository.save(newCashCardRequest.copy(owner = principal.name))
        val locationOfNewCashCard = URI.create("/cashcards/${savedCashCard.id}")
        return ResponseEntity.created(locationOfNewCashCard).build()
    }

    @PutMapping("/{requestedId}")
    private fun putCashCard(
        @PathVariable requestedId: Long,
        @RequestBody cashCardUpdate: CashCard,
        principal: Principal
    ): ResponseEntity<Void> {
        val cashCard = findCashCard(requestedId, principal)
        cashCard?.let {
            val updatedCashCard = it.copy(
                amount = cashCardUpdate.amount
            )
            cashCardRepository.save(updatedCashCard)
            return ResponseEntity.noContent().build()
        } ?: return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    private fun deleteCashCard(
        @PathVariable id: Long,
        principal: Principal
    ): ResponseEntity<Void> {
        return if (cashCardRepository.existsByIdAndOwner(id, principal.name)) {
            cashCardRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else
            ResponseEntity.notFound().build()
    }
}