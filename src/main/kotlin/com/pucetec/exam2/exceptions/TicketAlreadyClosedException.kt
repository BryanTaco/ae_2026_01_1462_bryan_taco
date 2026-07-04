package com.pucetec.exam2.exceptions

class TicketAlreadyClosedException(id: Long) : RuntimeException("Ticket $id is already closed")
