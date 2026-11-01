package com.ticketing.service;

import com.ticketing.model.Booking;
import com.ticketing.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to manage events and ticket bookings.
 */
public class BookingService {
    private final List<Event> events = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public BookingService() {
        loadDefaultEvents();
    }

    private void loadDefaultEvents() {
        events.add(new Event(
                "EVT-101",
                "Echoes in the Valley: Indie Music & Arts",
                "Concert",
                "Fri, 20 Nov 2026 • 05:00 PM",
                "Greenfield Open Arena, Sector 44",
                "Bengaluru, IN",
                "3-stage open air festival featuring 18 breakthrough indie acts, food pop-ups, art installations, and campfire acoustic sessions.",
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80",
                "Selling Fast 🔥",
                899.00,
                250
        ));

        events.add(new Event(
                "EVT-102",
                "NextGen Dev Con & AI Hackathon",
                "Tech",
                "Sat, 28 Nov 2026 • 09:30 AM",
                "Grand Silicon Convention Centre",
                "Hyderabad, IN",
                "A premier 2-day engineering gathering for builders. Deep dives on LLM systems, rust infrastructure, distributed architectures, and networking.",
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1200&q=80",
                "Early Bird ⚡",
                1499.00,
                180
        ));

        events.add(new Event(
                "EVT-103",
                "National Super Cup Derby: Finals",
                "Sports",
                "Sun, 06 Dec 2026 • 07:00 PM",
                "Jawaharlal Nehru Stadium - Gate 4",
                "New Delhi, IN",
                "The season's biggest football rivalry showdown under prime stadium floodlights. Electrifying crowd, halftime shows, and VIP fan zones.",
                "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80",
                "High Demand",
                1199.00,
                120
        ));

        events.add(new Event(
                "EVT-104",
                "Late Night Roast & Stand-Up Showcase",
                "Comedy",
                "Sat, 12 Dec 2026 • 08:30 PM",
                "The Canvas Laugh Club, Bandra West",
                "Mumbai, IN",
                "An unfiltered, no-holds-barred comedy evening with 5 of the country's sharpest touring stand-up acts in an intimate brick-wall room.",
                "https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=1200&q=80",
                "Almost Full",
                499.00,
                85
        ));

        events.add(new Event(
                "EVT-105",
                "The Artisan Brew & Street Food Carnival",
                "Food & Drinks",
                "Sun, 20 Dec 2026 • 12:00 PM",
                "Royal Palms Heritage Lawns",
                "Pune, IN",
                "Explore 40+ handcrafted micro-breweries, artisanal bakers, smoked BBQ counters, live jazz quartets, and chef tasting masterclasses.",
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80",
                "Weekend Pick",
                399.00,
                300
        ));

        events.add(new Event(
                "EVT-106",
                "Cinema Under The Stars: 35mm Retro Night",
                "Film & Arts",
                "Fri, 25 Dec 2026 • 07:30 PM",
                "Skyline Rooftop Amphitheatre, Cyber Hub",
                "Gurugram, IN",
                "Sink into plush beanbags under a starry winter sky for a 35mm classic cult cinema screening, complete with headphones and craft cider.",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80",
                "Exclusive",
                599.00,
                90
        ));
    }

    public List<Event> getAllEvents() {
        return Collections.unmodifiableList(events);
    }

    public Optional<Event> getEventById(String eventId) {
        return events.stream()
                .filter(e -> e.getId().equalsIgnoreCase(eventId))
                .findFirst();
    }

    public synchronized Booking bookTickets(String eventId, String customerName, String contact, int quantity) throws IllegalArgumentException {
        return bookTickets(eventId, customerName, contact, "General Admission", quantity);
    }

    public synchronized Booking bookTickets(String eventId, String customerName, String contact, String tier, int quantity) throws IllegalArgumentException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact detail cannot be empty.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ticket quantity must be at least 1.");
        }

        Event event = getEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        if (!event.hasAvailableTickets(quantity)) {
            throw new IllegalArgumentException("Not enough tickets available. Remaining tickets: " + event.getAvailableTickets());
        }

        boolean success = event.reserveTickets(quantity);
        if (!success) {
            throw new IllegalArgumentException("Failed to reserve tickets. Please try again.");
        }

        // Tier price multiplier: VIP is 1.8x, Squad is 3.4x (pack of 4), else 1.0x
        double unitPrice = event.getPrice();
        String selectedTier = (tier != null && !tier.trim().isEmpty()) ? tier.trim() : "General Admission";
        if (selectedTier.equalsIgnoreCase("VIP Access") || selectedTier.equalsIgnoreCase("VIP")) {
            unitPrice = Math.round(event.getPrice() * 1.8);
        } else if (selectedTier.toLowerCase().contains("squad") || selectedTier.toLowerCase().contains("group")) {
            unitPrice = Math.round(event.getPrice() * 3.4);
        }

        double totalCost = quantity * unitPrice;
        String bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking(bookingId, event.getId(), event.getTitle(), 
                customerName.trim(), contact.trim(), selectedTier, quantity, totalCost);
        bookings.add(0, booking); // Add newest on top
        return booking;
    }

    public synchronized boolean cancelBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookings.stream()
                .filter(b -> b.getBookingId().equalsIgnoreCase(bookingId))
                .findFirst();

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            getEventById(booking.getEventId()).ifPresent(event -> {
                event.restoreTickets(booking.getQuantity());
            });
            bookings.remove(booking);
            return true;
        }
        return false;
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(bookings);
    }
}
