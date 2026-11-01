# Event Ticket Booking System (Java Website)

A basic, clean, and beginner-friendly **Event Ticket Booking Website** built in Java. It features a straightforward, light-themed user interface with two clear tabs: browsing/booking events and managing reservations in Indian Rupees (`₹`).

---

## 🌟 Basic & Clean UI Features

1. **Tab 1: Available Events & Booking**:
   - **Clean Events Table**: Lists Event ID, Title, Category, Date & Time, Venue, Price in ₹ (INR), and Available Seats.
   - **Row Selection**: Clicking any row or the **Select** button highlights the row and automatically fills the booking form below.
   - **Simple Booking Form**:
     - Displays the selected event and per-ticket price.
     - Input fields: Customer Name, Email / Phone, and Ticket Quantity.
     - Live dynamic total price calculation.
     - **Clear Form** and **Confirm Booking** buttons.
   - **Booking Receipt Dialog**: A simple popup showing the confirmed ticket reference ID, event name, customer details, and total amount paid.

2. **Tab 2: My Bookings**:
   - Displays all confirmed bookings in a structured table.
   - **View Receipt**: Displays the booking receipt again anytime.
   - **Cancel Booking**: Cancels the reservation and immediately restores the tickets to the event pool.

3. **Built-in Java Web Server**:
   - Uses the Java Standard Library (`com.sun.net.httpserver.HttpServer`).
   - Requires **zero external server setup** (no Tomcat or Spring Boot needed).

---

## 📁 Project Structure

```text
at_ticketing/
├── pom.xml                                      # Maven configuration
├── run.bat                                      # 1-Click launcher to start website & open browser
├── build.bat                                    # Build & unit test runner
├── web/                                         # Basic Web Files
│   ├── index.html                               # Clean, basic HTML structure with 2 tabs
│   ├── styles.css                               # Light-themed, readable, clean CSS
│   └── app.js                                   # Simple JavaScript logic for table, form, and receipts
├── src/
│   ├── main/
│   │   ├── java/com/ticketing/
│   │   │   ├── Main.java                        # Starts web server and opens browser
│   │   │   ├── WebServer.java                   # Built-in Java HTTP server & REST API
│   │   │   ├── model/
│   │   │   │   ├── Event.java                   # Event model
│   │   │   │   └── Booking.java                 # Booking model & receipt generator
│   │   │   └── service/
│   │   │       └── BookingService.java          # Core booking & cancellation logic
│   │   └── resources/web/                       # Packaged web assets
│   └── test/java/com/ticketing/service/
│       └── BookingServiceTest.java              # Unit tests (JUnit 5)
```

---

## 🚀 How to Run Locally

### Option 1: Double-Click (Easiest)
Double-click **[`run.bat`](file:///c:/Users/ADITYA/OneDrive/Desktop/at_ticketing/run.bat)** in this project folder.
It starts the server and automatically opens your browser to:
👉 **`http://localhost:8080/`**

### Option 2: Via Terminal (Maven)
```bash
mvn compile exec:java
```
Then visit **`http://localhost:8080/`**.

### Option 3: Direct Browser Open (Offline)
Double-click **[`web/index.html`](file:///c:/Users/ADITYA/OneDrive/Desktop/at_ticketing/web/index.html)** to test the website directly in any browser.
