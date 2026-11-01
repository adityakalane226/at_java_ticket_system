// Basic and clean Event Ticket Booking Client Script

let eventsList = [];
let bookingsList = [];
let selectedEvent = null;

// Default events fallback if backend server isn't reached
const DEFAULT_EVENTS = [
  { id: "EVT-101", title: "Global Tech Conference 2026", category: "Tech", date: "2026-11-15 09:00 AM", venue: "Convention Center Hall A", price: 1499.00, totalTickets: 150, availableTickets: 150 },
  { id: "EVT-102", "title": "Acoustic Sunset Music Festival", category: "Concert", date: "2026-11-20 06:00 PM", venue: "Greenfield Amphitheater", price: 799.00, totalTickets: 200, availableTickets: 200 },
  { id: "EVT-103", title: "Championship Football Final", category: "Sports", date: "2026-11-28 07:30 PM", venue: "Metro Stadium Gate 3", price: 999.00, totalTickets: 100, availableTickets: 100 },
  { id: "EVT-104", title: "Stand-Up Comedy Night Live", category: "Comedy", date: "2026-12-05 08:00 PM", venue: "Downtown Laugh Club", price: 499.00, totalTickets: 80, availableTickets: 80 },
  { id: "EVT-105", title: "International Food & Wine Expo", category: "Exhibition", date: "2026-12-12 11:00 AM", venue: "City Grand Pavilion", price: 350.00, totalTickets: 120, availableTickets: 120 }
];

document.addEventListener("DOMContentLoaded", () => {
  loadEvents();
  loadBookings();
});

// Switch Tabs
function switchTab(tabName) {
  document.getElementById("tabEventsBtn").classList.toggle("active", tabName === "events");
  document.getElementById("tabBookingsBtn").classList.toggle("active", tabName === "bookings");

  document.getElementById("eventsTab").classList.toggle("active", tabName === "events");
  document.getElementById("bookingsTab").classList.toggle("active", tabName === "bookings");

  if (tabName === "bookings") {
    loadBookings();
  }
}

// Load Events from API or fallback
async function loadEvents() {
  try {
    const res = await fetch("/api/events");
    if (res.ok) {
      eventsList = await res.json();
    } else {
      throw new Error();
    }
  } catch (err) {
    const cached = localStorage.getItem("basic_events");
    eventsList = cached ? JSON.parse(cached) : DEFAULT_EVENTS;
  }
  renderEventsTable();
}

// Render Events Table
function renderEventsTable() {
  const tbody = document.getElementById("eventsTableBody");
  tbody.innerHTML = "";

  eventsList.forEach(event => {
    const tr = document.createElement("tr");
    if (selectedEvent && selectedEvent.id === event.id) {
      tr.className = "selected-row";
    }

    tr.onclick = (e) => {
      // Don't double trigger if clicking button
      if (e.target.tagName !== "BUTTON") {
        selectEvent(event.id);
      }
    };

    const isSoldOut = event.availableTickets <= 0;

    tr.innerHTML = `
      <td class="center-text"><strong>${escapeHtml(event.id)}</strong></td>
      <td>${escapeHtml(event.title)}</td>
      <td class="center-text"><span class="cat-badge">${escapeHtml(event.category)}</span></td>
      <td>${escapeHtml(event.date)}</td>
      <td>${escapeHtml(event.venue)}</td>
      <td class="center-text price-text">₹${event.price.toFixed(2)}</td>
      <td class="center-text">${event.availableTickets} / ${event.totalTickets}</td>
      <td class="center-text">
        <button class="btn btn-secondary btn-sm" onclick="selectEvent('${event.id}')" ${isSoldOut ? 'disabled' : ''}>
          ${isSoldOut ? 'Sold Out' : 'Select'}
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

// Select an Event
function selectEvent(eventId) {
  selectedEvent = eventsList.find(e => e.id === eventId);
  if (!selectedEvent) return;

  renderEventsTable();

  const banner = document.getElementById("selectedEventText");
  banner.textContent = `${selectedEvent.title} (₹${selectedEvent.price.toFixed(2)} per ticket) — Available Seats: ${selectedEvent.availableTickets}`;

  const qtyInput = document.getElementById("ticketQuantity");
  qtyInput.max = Math.max(1, selectedEvent.availableTickets);
  if (parseInt(qtyInput.value) > selectedEvent.availableTickets) {
    qtyInput.value = Math.max(1, selectedEvent.availableTickets);
  }

  updateTotal();

  // Scroll smoothly to the booking form
  document.getElementById("bookingBox").scrollIntoView({ behavior: "smooth" });
}

// Update Total Price
function updateTotal() {
  const display = document.getElementById("totalPriceDisplay");
  if (!selectedEvent) {
    display.textContent = "₹0.00";
    return;
  }

  const qty = parseInt(document.getElementById("ticketQuantity").value) || 1;
  const total = qty * selectedEvent.price;
  display.textContent = `₹${total.toFixed(2)}`;
}

// Reset Booking Form
function resetBookingForm() {
  selectedEvent = null;
  document.getElementById("selectedEventText").textContent = "Please select an event from the table above";
  document.getElementById("customerName").value = "";
  document.getElementById("customerContact").value = "";
  document.getElementById("ticketQuantity").value = "1";
  document.getElementById("totalPriceDisplay").textContent = "₹0.00";
  renderEventsTable();
}

// Handle Form Submission
async function handleBooking(e) {
  e.preventDefault();

  if (!selectedEvent) {
    alert("Please select an event from the table first.");
    return;
  }

  const name = document.getElementById("customerName").value.trim();
  const contact = document.getElementById("customerContact").value.trim();
  const qty = parseInt(document.getElementById("ticketQuantity").value) || 1;

  if (!name || !contact) {
    alert("Please fill in all required fields.");
    return;
  }

  if (qty > selectedEvent.availableTickets) {
    alert(`Only ${selectedEvent.availableTickets} tickets are available.`);
    return;
  }

  const payload = {
    eventId: selectedEvent.id,
    customerName: name,
    contact: contact,
    quantity: qty
  };

  try {
    const res = await fetch("/api/book", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      const data = await res.json();
      showReceiptModal(data.receipt || generateSimpleReceipt(data));
      loadEvents();
      loadBookings();
      resetBookingForm();
      return;
    }
  } catch (err) {
    // Offline fallback
  }

  // Fallback local booking
  selectedEvent.availableTickets -= qty;
  const bookingId = "BK-" + Math.random().toString(36).substring(2, 10).toUpperCase();
  const booking = {
    bookingId: bookingId,
    eventId: selectedEvent.id,
    eventTitle: selectedEvent.title,
    customerName: name,
    customerContact: contact,
    quantity: qty,
    totalCost: qty * selectedEvent.price,
    bookingTime: new Date().toLocaleString()
  };

  const savedBookings = JSON.parse(localStorage.getItem("basic_bookings") || "[]");
  savedBookings.unshift(booking);
  localStorage.setItem("basic_bookings", JSON.stringify(savedBookings));
  localStorage.setItem("basic_events", JSON.stringify(eventsList));

  showReceiptModal(generateSimpleReceipt(booking));
  renderEventsTable();
  loadBookings();
  resetBookingForm();
}

// Load and Render Confirmed Bookings
async function loadBookings() {
  try {
    const res = await fetch("/api/bookings");
    if (res.ok) {
      bookingsList = await res.json();
    } else {
      throw new Error();
    }
  } catch (err) {
    bookingsList = JSON.parse(localStorage.getItem("basic_bookings") || "[]");
  }

  document.getElementById("bookingCountBadge").textContent = bookingsList.length;

  const tbody = document.getElementById("bookingsTableBody");
  const noMsg = document.getElementById("noBookingsMsg");
  tbody.innerHTML = "";

  if (bookingsList.length === 0) {
    noMsg.classList.remove("hidden");
    return;
  }

  noMsg.classList.add("hidden");

  bookingsList.forEach(b => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="center-text"><strong>${escapeHtml(b.bookingId)}</strong></td>
      <td>${escapeHtml(b.eventTitle)}</td>
      <td>${escapeHtml(b.customerName)}</td>
      <td>${escapeHtml(b.customerContact)}</td>
      <td class="center-text">${b.quantity}</td>
      <td class="center-text price-text">₹${b.totalCost.toFixed(2)}</td>
      <td>${escapeHtml(b.bookingTime)}</td>
      <td class="center-text">
        <button class="btn btn-secondary btn-sm" onclick="viewReceipt('${b.bookingId}')">View Receipt</button>
        <button class="btn btn-danger btn-sm" onclick="cancelBooking('${b.bookingId}')">Cancel</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

// View Receipt
function viewReceipt(bookingId) {
  const b = bookingsList.find(x => x.bookingId === bookingId);
  if (b) {
    showReceiptModal(generateSimpleReceipt(b));
  }
}

// Cancel Booking
async function cancelBooking(bookingId) {
  const b = bookingsList.find(x => x.bookingId === bookingId);
  if (!b) return;

  if (!confirm(`Are you sure you want to cancel booking ${bookingId} for "${b.eventTitle}"?\nTickets will be restored to the event.`)) {
    return;
  }

  try {
    const res = await fetch("/api/cancel", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ bookingId })
    });
    if (res.ok) {
      alert("Booking cancelled successfully! Tickets have been returned to the event.");
      loadEvents();
      loadBookings();
      return;
    }
  } catch (err) {}

  // Local fallback
  const evt = eventsList.find(e => e.id === b.eventId);
  if (evt) {
    evt.availableTickets = Math.min(evt.totalTickets, evt.availableTickets + b.quantity);
  }
  bookingsList = bookingsList.filter(x => x.bookingId !== bookingId);
  localStorage.setItem("basic_bookings", JSON.stringify(bookingsList));
  localStorage.setItem("basic_events", JSON.stringify(eventsList));

  alert("Booking cancelled successfully! Tickets have been returned to the event.");
  renderEventsTable();
  loadBookings();
}

// Receipt Modal Helpers
function showReceiptModal(receiptText) {
  document.getElementById("receiptText").textContent = receiptText;
  document.getElementById("receiptModal").classList.remove("hidden");
}

function closeReceiptModal() {
  document.getElementById("receiptModal").classList.add("hidden");
}

function generateSimpleReceipt(b) {
  return "==========================================\n"
       + "             BOOKING RECEIPT              \n"
       + "==========================================\n"
       + "Booking Reference : " + b.bookingId + "\n"
       + "Event             : " + b.eventTitle + "\n"
       + "Customer Name     : " + b.customerName + "\n"
       + "Contact           : " + b.customerContact + "\n"
       + "Tickets Booked    : " + b.quantity + "\n"
       + "Total Amount Paid : ₹" + b.totalCost.toFixed(2) + "\n"
       + "Date & Time       : " + b.bookingTime + "\n"
       + "Status            : CONFIRMED\n"
       + "==========================================\n"
       + "   Please present this ticket at entry!   \n"
       + "==========================================";
}

function escapeHtml(text) {
  if (!text) return "";
  return String(text).replace(/[&<>"']/g, m => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[m]);
}
