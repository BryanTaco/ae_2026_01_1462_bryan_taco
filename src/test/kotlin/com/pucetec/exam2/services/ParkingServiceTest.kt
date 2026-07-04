package com.pucetec.exam2.services

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.entities.ParkingSpace
import com.pucetec.exam2.entities.Ticket
import com.pucetec.exam2.exceptions.ParkingFullException
import com.pucetec.exam2.exceptions.ParkingSpaceNotFoundException
import com.pucetec.exam2.exceptions.SpaceAlreadyOccupiedException
import com.pucetec.exam2.exceptions.TicketAlreadyClosedException
import com.pucetec.exam2.exceptions.TicketNotFoundException
import com.pucetec.exam2.repositories.ParkingSpaceRepository
import com.pucetec.exam2.repositories.TicketRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ParkingServiceTest {

    @Mock
    private lateinit var parkingSpaceRepository: ParkingSpaceRepository

    @Mock
    private lateinit var ticketRepository: TicketRepository

    @InjectMocks
    private lateinit var parkingService: ParkingService

    private val availableSpace = ParkingSpace(id = 1L, code = "A1", available = true)
    private val occupiedSpace = ParkingSpace(id = 2L, code = "A2", available = false)

    // ─── getAvailableSpaces ───────────────────────────────────────────────────

    @Test
    fun `getAvailableSpaces retorna lista de espacios disponibles`() {
        `when`(parkingSpaceRepository.findByAvailableTrue()).thenReturn(listOf(availableSpace))

        val result = parkingService.getAvailableSpaces()

        assertEquals(1, result.size)
        assertEquals("A1", result[0].code)
        assertTrue(result[0].available)
    }

    // ─── registerEntry ────────────────────────────────────────────────────────

    @Test
    fun `registerEntry lanza ParkingSpaceNotFoundException cuando el espacio no existe`() {
        val request = EntryRequest(licensePlate = "ABC-123", spaceId = 99L)
        `when`(parkingSpaceRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(ParkingSpaceNotFoundException::class.java) {
            parkingService.registerEntry(request)
        }
    }

    @Test
    fun `registerEntry lanza ParkingFullException cuando el estacionamiento esta lleno`() {
        val request = EntryRequest(licensePlate = "ABC-123", spaceId = 1L)
        `when`(parkingSpaceRepository.findById(1L)).thenReturn(Optional.of(availableSpace))
        `when`(parkingSpaceRepository.countByAvailableFalse()).thenReturn(20L)

        assertThrows(ParkingFullException::class.java) {
            parkingService.registerEntry(request)
        }
    }

    @Test
    fun `registerEntry lanza SpaceAlreadyOccupiedException cuando el espacio ya esta ocupado`() {
        val request = EntryRequest(licensePlate = "ABC-123", spaceId = 2L)
        `when`(parkingSpaceRepository.findById(2L)).thenReturn(Optional.of(occupiedSpace))
        `when`(parkingSpaceRepository.countByAvailableFalse()).thenReturn(10L)

        assertThrows(SpaceAlreadyOccupiedException::class.java) {
            parkingService.registerEntry(request)
        }
    }

    @Test
    fun `registerEntry crea el ticket y ocupa el espacio cuando los datos son validos`() {
        val request = EntryRequest(licensePlate = "ABC-123", spaceId = 1L)
        val space = ParkingSpace(id = 1L, code = "A1", available = true)
        val savedTicket = Ticket(id = 10L, licensePlate = "ABC-123", entryTime = LocalDateTime.now(), parkingSpace = space)

        `when`(parkingSpaceRepository.findById(1L)).thenReturn(Optional.of(space))
        `when`(parkingSpaceRepository.countByAvailableFalse()).thenReturn(5L)
        `when`(parkingSpaceRepository.save(any())).thenReturn(space)
        `when`(ticketRepository.save(any())).thenReturn(savedTicket)

        val result = parkingService.registerEntry(request)

        assertEquals(10L, result.id)
        assertEquals("ABC-123", result.licensePlate)
        assertNull(result.exitTime)
        assertEquals("A1", result.parkingSpace.code)
        assertTrue(!space.available)
        verify(parkingSpaceRepository).save(space)
    }

    // ─── registerExit ─────────────────────────────────────────────────────────

    @Test
    fun `registerExit lanza TicketNotFoundException cuando el ticket no existe`() {
        `when`(ticketRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(TicketNotFoundException::class.java) {
            parkingService.registerExit(99L)
        }
    }

    @Test
    fun `registerExit lanza TicketAlreadyClosedException cuando el ticket ya esta cerrado`() {
        val closedTicket = Ticket(
            id = 1L,
            licensePlate = "XYZ-999",
            entryTime = LocalDateTime.now().minusHours(2),
            exitTime = LocalDateTime.now().minusHours(1),
            parkingSpace = availableSpace,
        )
        `when`(ticketRepository.findById(1L)).thenReturn(Optional.of(closedTicket))

        assertThrows(TicketAlreadyClosedException::class.java) {
            parkingService.registerExit(1L)
        }
    }

    @Test
    fun `registerExit cierra el ticket y libera el espacio cuando los datos son validos`() {
        val space = ParkingSpace(id = 1L, code = "A1", available = false)
        val openTicket = Ticket(
            id = 1L,
            licensePlate = "ABC-123",
            entryTime = LocalDateTime.now().minusHours(1),
            exitTime = null,
            parkingSpace = space,
        )
        val closedTicket = Ticket(
            id = 1L,
            licensePlate = "ABC-123",
            entryTime = openTicket.entryTime,
            exitTime = LocalDateTime.now(),
            parkingSpace = space,
        )

        `when`(ticketRepository.findById(1L)).thenReturn(Optional.of(openTicket))
        `when`(parkingSpaceRepository.save(any())).thenReturn(space)
        `when`(ticketRepository.save(any())).thenReturn(closedTicket)

        val result = parkingService.registerExit(1L)

        assertNotNull(result.exitTime)
        assertTrue(space.available)
        verify(parkingSpaceRepository).save(space)
        verify(ticketRepository).save(openTicket)
    }
}
