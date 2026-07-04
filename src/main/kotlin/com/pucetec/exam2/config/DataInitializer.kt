package com.pucetec.exam2.config

import com.pucetec.exam2.entities.ParkingSpace
import com.pucetec.exam2.repositories.ParkingSpaceRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val parkingSpaceRepository: ParkingSpaceRepository) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (parkingSpaceRepository.count() == 0L) {
            val spaces = (1..20).map { i -> ParkingSpace(code = "P${i.toString().padStart(2, '0')}") }
            parkingSpaceRepository.saveAll(spaces)
        }
    }
}
