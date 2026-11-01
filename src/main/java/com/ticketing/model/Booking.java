package com.ticketing.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a ticket booking made by a customer.
 */
public class Booking {
    private final String bookingId;
    private final String eventId;
    private final String eventTitle;
    private final String customerName;
    private final String customerContact;
    private final String ticketTier;
    private final int quantity;
    private final double totalCost;
    private final LocalDateTime bookingTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Booking(String bookingId, String eventId, String eventTitle, String customerName, 
                   String customerContact, String ticketTier, int quantity, double totalCost) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.ticketTier = (ticketTier != null && !ticketTier.trim().isEmpty()) ? ticketTier : "General Admission";
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.bookingTime = LocalDateTime.now();
    }

    public Booking(String bookingId, String eventId, String eventTitle, String customerName, 
                   String customerContact, int quantity, double totalCost) {
        this(bookingId, eventId, eventTitle, customerName, customerContact, "General Admission", quantity, totalCost);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public String getTicketTier() {
        return ticketTier;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public String getFormattedBookingTime() {
        return bookingTime.format(FORMATTER);
    }

    public String generateReceipt() {
        return "==========================================\n"
             + "             BOOKING RECEIPT              \n"
             + "==========================================\n"
             + "Booking Reference : " + bookingId + "\n"
             + "Event             : " + eventTitle + "\n"
             + "Customer Name     : " + customerName + "\n"
             + "Contact           : " + customerContact + "\n"
             + "Ticket Tier       : " + ticketTier + "\n"
             + "Tickets Booked    : " + quantity + "\n"
             + "Total Amount Paid : ₹" + String.format("%.2f", totalCost) + "\n"
             + "Date & Time       : " + getFormattedBookingTime() + "\n"
             + "Status            : CONFIRMED\n"
             + "==========================================\n"
             + "   Please present this ticket at entry!   \n"
             + "==========================================";
    }
}
