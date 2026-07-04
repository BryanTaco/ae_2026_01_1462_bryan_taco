package com.pucetec.exam2.exceptions

class ParkingSpaceNotFoundException(id: Long) : RuntimeException("Parking space with id $id not found")
