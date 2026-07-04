package com.pucetec.exam2.services

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.dto.ParkingSpaceResponse
import com.pucetec.exam2.dto.TicketResponse
import com.pucetec.exam2.entities.Ticket
import com.pucetec.exam2.exceptions.ParkingFullException
import com.pucetec.exam2.exceptions.ParkingSpaceNotFoundException
import com.pucetec.exam2.exceptions.SpaceAlreadyOccupiedException
import com.pucetec.exam2.exceptions.TicketAlreadyClosedException
import com.pucetec.exam2.exceptions.TicketNotFoundException
import com.pucetec.exam2.mappers.toResponse
import com.pucetec.exam2.repositories.ParkingSpaceRepository
import com.pucetec.exam2.repositories.TicketRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ParkingService(
    private val parkingSpaceRepository: ParkingSpaceRepository,
    private val ticketRepository: TicketRepository,
) {
    private val logger = LoggerFactory.getLogger(ParkingService::class.java)
    private val maxCapacity = 20L

    fun getAvailableSpaces(): List<ParkingSpaceResponse> {
        logger.info("Getting available spaces")
        return parkingSpaceRepository.findByAvailableTrue().map { it.toResponse() }
    }

    fun registerEntry(request: EntryRequest): TicketResponse {
        val space = parkingSpaceRepository.findById(request.spaceId)
            .orElseThrow { ParkingSpaceNotFoundException(request.spaceId) }

        val occupiedCount = parkingSpaceRepository.countByAvailableFalse()
        if (occupiedCount >= maxCapacity) {
            throw ParkingFullException("Parking lot is full (max $maxCapacity spaces)")
        }

        if (!space.available) {
            throw SpaceAlreadyOccupiedException(space.code)
        }

        space.available = false
        parkingSpaceRepository.save(space)

        logger.info("Vehicle ${request.licensePlate} entered space ${space.code}")
        val ticket = Ticket(
            licensePlate = request.licensePlate,
            entryTime = LocalDateTime.now(),
            parkingSpace = space,
        )
        return ticketRepository.save(ticket).toResponse()
    }

    fun registerExit(ticketId: Long): TicketResponse {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { TicketNotFoundException(ticketId) }

        if (ticket.exitTime != null) {
            throw TicketAlreadyClosedException(ticketId)
        }

        ticket.exitTime = LocalDateTime.now()
        val space = ticket.parkingSpace!!
        space.available = true
        parkingSpaceRepository.save(space)

        logger.info("Vehicle ${ticket.licensePlate} exited space ${space.code}")
        return ticketRepository.save(ticket).toResponse()
    }
}
