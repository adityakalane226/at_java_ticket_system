package com.ticketing.ui;

import com.ticketing.model.Booking;
import com.ticketing.model.Event;
import com.ticketing.service.BookingService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Colorful and modern Swing-based GUI for the Event Ticket Booking System.
 */
public class TicketBookingUI extends JFrame {

    private final BookingService bookingService;

    // Header with live stats
    private GradientHeaderPanel headerPanel;

    // Events Tab Components
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    private JLabel selectedEventLabel;
    private JTextField nameField;
    private JTextField contactField;
    private JSpinner quantitySpinner;
    private JLabel totalPriceLabel;
    private Event currentSelectedEvent = null;

    // Bookings Tab Components
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;

    public TicketBookingUI(BookingService bookingService) {
        this.bookingService = bookingService;
        initUI();
    }

    private void initUI() {
        setTitle("Event Ticket Booking System");
        setSize(1000, 680);
        setMinimumSize(new Dimension(860, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gradient Header Panel with live stats
        headerPanel = new GradientHeaderPanel();

        // Modern Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(UITheme.CANVAS_BG);

        tabbedPane.addTab("🎪 Available Events & Booking", createEventsPanel());
        tabbedPane.addTab("📋 Booking History", createBookingsPanel());

        // Main Frame Layout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.CANVAS_BG);
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        getContentPane().add(mainContainer);

        // Initial Data Load
        refreshEventsTable();
        refreshBookingsTable();
        updateGlobalStats();
    }

    /**
     * Builds the Events list and Booking form panel.
     */
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(UITheme.CANVAS_BG);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Table of Events
        String[] columns = {"Event ID", "Event Title", "Category", "Date & Time", "Venue", "Price (₹)", "Available Seats"};
        eventsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventsTable = new JTable(eventsTableModel);
        styleTable(eventsTable);

        // Center align ID
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        eventsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        eventsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(200);

        // Category Badge Renderer
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventsTable.getColumnModel().getColumn(2).setCellRenderer(new CategoryBadgeRenderer());

        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        // Price formatting
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(isSelected ? new Color(30, 27, 75) : new Color(5, 150, 105)); // Emerald
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        eventsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        eventsTable.getColumnModel().getColumn(5).setCellRenderer(priceRenderer);

        // Seats Badge Renderer
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(new SeatsBadgeRenderer());

        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleEventSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                " 🎪 Click an event to select & book tickets ",
                TitledBorder.LEFT, TitledBorder.TOP, UITheme.FONT_BOLD, UITheme.PRIMARY));

        // 2. Booking Form Panel (Styled Card)
        JPanel formCard = new JPanel(new BorderLayout(10, 10));
        formCard.setBackground(UITheme.CARD_BG);
        formCard.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                        " 🎟️ Booking Details ",
                        TitledBorder.LEFT, TitledBorder.TOP, UITheme.FONT_BOLD, UITheme.PRIMARY),
                new EmptyBorder(8, 12, 8, 12)));

        // Event banner inside form
        JPanel eventBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        eventBanner.setBackground(new Color(238, 242, 255)); // Soft indigo card
        eventBanner.setBorder(new LineBorder(new Color(199, 210, 254), 1, true));

        JLabel eventTag = new JLabel("Selected Event: ");
        eventTag.setFont(UITheme.FONT_BOLD);
        eventTag.setForeground(UITheme.PRIMARY);

        selectedEventLabel = new JLabel("Please select an event from the table above");
        selectedEventLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedEventLabel.setForeground(UITheme.TEXT_MAIN);

        eventBanner.add(eventTag);
        eventBanner.add(selectedEventLabel);

        // Input Form Fields
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Customer Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setFont(UITheme.FONT_BOLD);
        formGrid.add(nameLabel, gbc);

        nameField = new JTextField(18);
        nameField.setFont(UITheme.FONT_REGULAR);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1;
        formGrid.add(nameField, gbc);

        // Row 0: Contact
        gbc.gridx = 2;
        JLabel contactLabel = new JLabel("Email / Phone:");
        contactLabel.setFont(UITheme.FONT_BOLD);
        formGrid.add(contactLabel, gbc);

        contactField = new JTextField(18);
        contactField.setFont(UITheme.FONT_REGULAR);
        contactField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 3;
        formGrid.add(contactField, gbc);

        // Row 1: Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel qtyLabel = new JLabel("Ticket Quantity:");
        qtyLabel.setFont(UITheme.FONT_BOLD);
        formGrid.add(qtyLabel, gbc);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(UITheme.FONT_REGULAR);
        quantitySpinner.addChangeListener(e -> updateCalculatedTotal());
        gbc.gridx = 1;
        formGrid.add(quantitySpinner, gbc);

        // Row 1: Total Price Highlight
        gbc.gridx = 2;
        JLabel totalLabel = new JLabel("Total Price:");
        totalLabel.setFont(UITheme.FONT_BOLD);
        formGrid.add(totalLabel, gbc);

        totalPriceLabel = new JLabel("₹0.00");
        totalPriceLabel.setFont(UITheme.FONT_PRICE);
        totalPriceLabel.setForeground(UITheme.SUCCESS_HOVER);
        gbc.gridx = 3;
        formGrid.add(totalPriceLabel, gbc);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        buttonPanel.setOpaque(false);

        ModernButton clearBtn = new ModernButton("✕ Clear Form",
                new Color(226, 232, 240), new Color(203, 213, 225), UITheme.TEXT_MAIN);
        clearBtn.setPreferredSize(new Dimension(120, 36));
        clearBtn.addActionListener(e -> resetBookingForm());

        ModernButton bookBtn = new ModernButton("✓ Confirm Booking",
                UITheme.SUCCESS, UITheme.SUCCESS_HOVER, Color.WHITE);
        bookBtn.setPreferredSize(new Dimension(160, 36));
        bookBtn.addActionListener(e -> handleBooking());

        buttonPanel.add(clearBtn);
        buttonPanel.add(bookBtn);

        JPanel centerForm = new JPanel(new BorderLayout(8, 8));
        centerForm.setOpaque(false);
        centerForm.add(eventBanner, BorderLayout.NORTH);
        centerForm.add(formGrid, BorderLayout.CENTER);

        formCard.add(centerForm, BorderLayout.CENTER);
        formCard.add(buttonPanel, BorderLayout.SOUTH);

        // Split view
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formCard);
        splitPane.setResizeWeight(0.62);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the Bookings Management Panel.
     */
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(UITheme.CANVAS_BG);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        String[] columns = {"Booking ID", "Event Title", "Customer Name", "Contact", "Quantity", "Total Paid (₹)", "Booking Time"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        styleTable(bookingsTable);

        // Booking ID styled badge
        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(isSelected ? new Color(30, 27, 75) : UITheme.PRIMARY);
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        bookingsTable.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(140);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        bookingsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Total Paid Price renderer
        DefaultTableCellRenderer paidRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(isSelected ? new Color(30, 27, 75) : new Color(5, 150, 105));
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(5).setCellRenderer(paidRenderer);

        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(140);
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                " 📋 Confirmed Reservations & Ticket History ",
                TitledBorder.LEFT, TitledBorder.TOP, UITheme.FONT_BOLD, UITheme.PRIMARY));

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionPanel.setOpaque(false);

        ModernButton refreshBtn = new ModernButton("🔄 Refresh",
                UITheme.PRIMARY, UITheme.PRIMARY_HOVER, Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.addActionListener(e -> {
            refreshBookingsTable();
            updateGlobalStats();
        });

        ModernButton viewReceiptBtn = new ModernButton("📄 View Ticket Receipt",
                UITheme.INFO, UITheme.INFO_HOVER, Color.WHITE);
        viewReceiptBtn.setPreferredSize(new Dimension(170, 36));
        viewReceiptBtn.addActionListener(e -> handleViewReceipt());

        ModernButton cancelBtn = new ModernButton("✕ Cancel Selected Booking",
                UITheme.DANGER, UITheme.DANGER_HOVER, Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(200, 36));
        cancelBtn.addActionListener(e -> handleCancelBooking());

        actionPanel.add(refreshBtn);
        actionPanel.add(viewReceiptBtn);
        actionPanel.add(cancelBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Applies modern colorful styling to JTables.
     */
    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(UITheme.FONT_REGULAR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(224, 231, 255)); // Soft lavender highlight
        table.setSelectionForeground(new Color(30, 27, 75));
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                label.setBackground(UITheme.PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(CENTER);
                label.setBorder(new EmptyBorder(6, 4, 6, 4));
                label.setOpaque(true);
                return label;
            }
        });
    }

    private void handleEventSelection() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String eventId = (String) eventsTableModel.getValueAt(selectedRow, 0);
            bookingService.getEventById(eventId).ifPresent(event -> {
                currentSelectedEvent = event;
                selectedEventLabel.setText(event.getTitle() + "  |  Venue: " + event.getVenue() + "  |  ₹" + String.format("%.2f", event.getPrice()) + "/ticket");
                int available = event.getAvailableTickets();
                int max = Math.max(1, available);
                quantitySpinner.setModel(new SpinnerNumberModel(available > 0 ? 1 : 0, 0, max, 1));
                updateCalculatedTotal();
            });
        }
    }

    private void updateCalculatedTotal() {
        if (currentSelectedEvent != null) {
            int qty = (Integer) quantitySpinner.getValue();
            double total = qty * currentSelectedEvent.getPrice();
            totalPriceLabel.setText(String.format("₹%.2f", total));
        } else {
            totalPriceLabel.setText("₹0.00");
        }
    }

    private void handleBooking() {
        if (currentSelectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table first.", 
                    "No Event Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        int qty = (Integer) quantitySpinner.getValue();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email or phone number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            contactField.requestFocus();
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Please select at least 1 ticket.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Booking booking = bookingService.bookTickets(currentSelectedEvent.getId(), name, contact, qty);
            refreshEventsTable();
            refreshBookingsTable();
            updateGlobalStats();

            // Show confirmation dialog with ticket receipt
            JTextArea receiptArea = new JTextArea(booking.generateReceipt());
            receiptArea.setEditable(false);
            receiptArea.setFont(new Font("Monospaced", Font.BOLD, 13));
            receiptArea.setForeground(new Color(30, 27, 75));
            receiptArea.setBackground(new Color(245, 247, 255));
            receiptArea.setBorder(new EmptyBorder(12, 14, 12, 14));

            JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea), 
                    "🎉 Booking Confirmed! Ref: " + booking.getBookingId(), JOptionPane.INFORMATION_MESSAGE);

            resetBookingForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewReceipt() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to view receipt.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        bookingService.getAllBookings().stream()
                .filter(b -> b.getBookingId().equalsIgnoreCase(bookingId))
                .findFirst()
                .ifPresent(booking -> {
                    JTextArea receiptArea = new JTextArea(booking.generateReceipt());
                    receiptArea.setEditable(false);
                    receiptArea.setFont(new Font("Monospaced", Font.BOLD, 13));
                    receiptArea.setForeground(new Color(30, 27, 75));
                    receiptArea.setBackground(new Color(245, 247, 255));
                    receiptArea.setBorder(new EmptyBorder(12, 14, 12, 14));

                    JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea), 
                            "Ticket Receipt - " + booking.getBookingId(), JOptionPane.PLAIN_MESSAGE);
                });
    }

    private void handleCancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the list to cancel.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        String eventTitle = (String) bookingsTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking " + bookingId + " for \"" + eventTitle + "\"?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bookingService.cancelBooking(bookingId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully. Tickets have been released back.", 
                        "Cancellation Confirmed", JOptionPane.INFORMATION_MESSAGE);
                refreshEventsTable();
                refreshBookingsTable();
                updateGlobalStats();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetBookingForm() {
        eventsTable.clearSelection();
        currentSelectedEvent = null;
        selectedEventLabel.setText("Please select an event from the table above");
        nameField.setText("");
        contactField.setText("");
        quantitySpinner.setValue(1);
        totalPriceLabel.setText("₹0.00");
    }

    private void updateGlobalStats() {
        List<Event> events = bookingService.getAllEvents();
        int totalSeats = events.stream().mapToInt(Event::getAvailableTickets).sum();
        int bookingCount = bookingService.getAllBookings().size();
        headerPanel.updateStats(events.size(), totalSeats, bookingCount);
    }

    private void refreshEventsTable() {
        eventsTableModel.setRowCount(0);
        List<Event> events = bookingService.getAllEvents();
        for (Event event : events) {
            eventsTableModel.addRow(new Object[]{
                    event.getId(),
                    event.getTitle(),
                    event.getCategory(),
                    event.getDate(),
                    event.getVenue(),
                    "₹" + String.format("%.2f", event.getPrice()),
                    event.getAvailableTickets() + " / " + event.getTotalTickets()
            });
        }
    }

    private void refreshBookingsTable() {
        bookingsTableModel.setRowCount(0);
        List<Booking> bookings = bookingService.getAllBookings();
        for (Booking booking : bookings) {
            bookingsTableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getEventTitle(),
                    booking.getCustomerName(),
                    booking.getCustomerContact(),
                    booking.getQuantity(),
                    "₹" + String.format("%.2f", booking.getTotalCost()),
                    booking.getFormattedBookingTime()
            });
        }
    }
}
