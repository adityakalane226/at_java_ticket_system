package com.ticketing.model;

/**
 * Represents an event for which tickets can be booked.
 */
public class Event {
    private final String id;
    private final String title;
    private final String category;
    private final String date;
    private final String venue;
    private final String city;
    private final String description;
    private final String imageUrl;
    private final String badge;
    private final double price;
    private final int totalTickets;
    private int availableTickets;

    public Event(String id, String title, String category, String date, String venue, 
                 String city, String description, String imageUrl, String badge,
                 double price, int totalTickets) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.venue = venue;
        this.city = city;
        this.description = description;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.price = price;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
    }

    // Convenience constructor for backward compatibility
    public Event(String id, String title, String category, String date, String venue, double price, int totalTickets) {
        this(id, title, category, date, venue, "Mumbai, IN", 
             "Experience an unforgettable live event with premium production and curated crowd.", 
             "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80",
             "Popular", price, totalTickets);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getVenue() {
        return venue;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBadge() {
        return badge;
    }

    public double getPrice() {
        return price;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public synchronized int getAvailableTickets() {
        return availableTickets;
    }

    public synchronized boolean hasAvailableTickets(int quantity) {
        return quantity > 0 && availableTickets >= quantity;
    }

    public synchronized boolean reserveTickets(int quantity) {
        if (hasAvailableTickets(quantity)) {
            availableTickets -= quantity;
            return true;
        }
        return false;
    }

    public synchronized void restoreTickets(int quantity) {
        if (quantity > 0) {
            availableTickets = Math.min(totalTickets, availableTickets + quantity);
        }
    }

    @Override
    public String toString() {
        return title + " (" + date + ") - ₹" + String.format("%.2f", price);
    }
}
