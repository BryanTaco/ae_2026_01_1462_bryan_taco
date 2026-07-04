package com.pucetec.exam2.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(val message: String, val source: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ParkingSpaceNotFoundException::class)
    fun handleSpaceNotFound(ex: ParkingSpaceNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(ex.message ?: "Space not found", "ParkingSpaceNotFoundException"))

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(ex: TicketNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(ex.message ?: "Ticket not found", "TicketNotFoundException"))

    @ExceptionHandler(ParkingFullException::class)
    fun handleParkingFull(ex: ParkingFullException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Parking is full", "ParkingFullException"))

    @ExceptionHandler(SpaceAlreadyOccupiedException::class)
    fun handleSpaceOccupied(ex: SpaceAlreadyOccupiedException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Space already occupied", "SpaceAlreadyOccupiedException"))

    @ExceptionHandler(TicketAlreadyClosedException::class)
    fun handleTicketClosed(ex: TicketAlreadyClosedException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Ticket already closed", "TicketAlreadyClosedException"))
}
