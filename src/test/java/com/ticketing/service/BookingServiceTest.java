package com.ticketing.service;

import com.ticketing.model.Booking;
import com.ticketing.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService();
    }

    @Test
    void testInitialEventsLoaded() {
        assertFalse(service.getAllEvents().isEmpty(), "Default events should be loaded");
        assertEquals(6, service.getAllEvents().size(), "There should be 6 default events");
    }

    @Test
    void testSuccessfulBooking() {
        Event event = service.getAllEvents().get(0);
        int initialAvailable = event.getAvailableTickets();

        Booking booking = service.bookTickets(event.getId(), "John Doe", "john@example.com", 2);

        assertNotNull(booking);
        assertTrue(booking.getBookingId().startsWith("BK-"));
        assertEquals("John Doe", booking.getCustomerName());
        assertEquals(2, booking.getQuantity());
        assertEquals(2 * event.getPrice(), booking.getTotalCost(), 0.001);
        assertEquals(initialAvailable - 2, event.getAvailableTickets());
        assertEquals(1, service.getAllBookings().size());
    }

    @Test
    void testBookingFailsWhenQuantityExceedsAvailable() {
        Event event = service.getAllEvents().get(0);
        int tooMany = event.getAvailableTickets() + 10;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookTickets(event.getId(), "Jane Doe", "jane@example.com", tooMany);
        });

        assertTrue(exception.getMessage().contains("Not enough tickets"));
    }

    @Test
    void testBookingFailsOnBlankCustomerName() {
        Event event = service.getAllEvents().get(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookTickets(event.getId(), "   ", "jane@example.com", 1);
        });

        assertTrue(exception.getMessage().contains("Customer name cannot be empty"));
    }

    @Test
    void testCancelBookingRestoresTickets() {
        Event event = service.getAllEvents().get(0);
        int initialAvailable = event.getAvailableTickets();

        Booking booking = service.bookTickets(event.getId(), "Alice", "alice@example.com", 3);
        assertEquals(initialAvailable - 3, event.getAvailableTickets());

        boolean cancelled = service.cancelBooking(booking.getBookingId());
        assertTrue(cancelled, "Booking cancellation should succeed");
        assertEquals(initialAvailable, event.getAvailableTickets(), "Tickets should be returned to the event pool");
        assertEquals(0, service.getAllBookings().size(), "Bookings list should be empty");
    }

    @Test
    void testReceiptGeneration() {
        Event event = service.getAllEvents().get(0);
        Booking booking = service.bookTickets(event.getId(), "Alice", "alice@example.com", 1);
        String receipt = booking.generateReceipt();

        assertNotNull(receipt);
        assertTrue(receipt.contains("BOOKING RECEIPT"));
        assertTrue(receipt.contains(booking.getBookingId()));
        assertTrue(receipt.contains("Alice"));
    }
}
