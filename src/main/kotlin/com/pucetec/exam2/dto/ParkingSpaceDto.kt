package com.pucetec.exam2.dto

data class ParkingSpaceResponse(
    val id: Long,
    val code: String,
    val available: Boolean,
)
