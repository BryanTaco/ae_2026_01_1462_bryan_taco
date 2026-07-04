package com.pucetec.exam2.exceptions

class TicketNotFoundException(id: Long) : RuntimeException("Ticket with id $id not found")
