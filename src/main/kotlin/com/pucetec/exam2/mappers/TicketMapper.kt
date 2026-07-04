package com.pucetec.exam2.mappers

import com.pucetec.exam2.dto.TicketResponse
import com.pucetec.exam2.entities.Ticket

fun Ticket.toResponse(): TicketResponse = TicketResponse(
    id = id,
    licensePlate = licensePlate,
    entryTime = entryTime,
    exitTime = exitTime,
    parkingSpace = parkingSpace!!.toResponse(),
)
