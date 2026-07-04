package com.pucetec.exam2.exceptions

class SpaceAlreadyOccupiedException(code: String) : RuntimeException("Space $code is already occupied")
