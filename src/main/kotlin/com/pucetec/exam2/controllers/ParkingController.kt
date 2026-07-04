package com.pucetec.exam2.controllers

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.dto.ParkingSpaceResponse
import com.pucetec.exam2.dto.TicketResponse
import com.pucetec.exam2.services.ParkingService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/parking")
class ParkingController(
    private val parkingService: ParkingService,
) {
    private val logger = LoggerFactory.getLogger(ParkingController::class.java)

    @GetMapping("/spaces/available")
    fun getAvailableSpaces(): List<ParkingSpaceResponse> {
        logger.info("GET /api/parking/spaces/available")
        return parkingService.getAvailableSpaces()
    }

    @PostMapping("/entry")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerEntry(@RequestBody request: EntryRequest): TicketResponse {
        logger.info("POST /api/parking/entry - plate=${request.licensePlate} spaceId=${request.spaceId}")
        return parkingService.registerEntry(request)
    }

    @PutMapping("/exit/{ticketId}")
    fun registerExit(@PathVariable ticketId: Long): TicketResponse {
        logger.info("PUT /api/parking/exit/$ticketId")
        return parkingService.registerExit(ticketId)
    }
}
