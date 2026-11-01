package com.ticketing.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Colorful gradient banner with title and real-time summary stat pills.
 */
public class GradientHeaderPanel extends JPanel {

    private final JLabel eventsStatLabel;
    private final JLabel seatsStatLabel;
    private final JLabel bookingsStatLabel;

    public GradientHeaderPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(16, 22, 16, 22));

        // Left: Title and subtitle
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("🎟️ Event Ticket Booking System");
        titleLabel.setFont(UITheme.FONT_HEADER_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Explore events, book instantly in ₹ (INR), and manage your reservations");
        subLabel.setFont(UITheme.FONT_HEADER_SUB);
        subLabel.setForeground(new Color(237, 233, 254)); // Soft lavender

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(subLabel);

        // Right: Colorful Stat Badges
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);

        eventsStatLabel = createStatBadge("🎪 Events: 0", new Color(255, 255, 255, 45));
        seatsStatLabel = createStatBadge("🎫 Seats: 0", new Color(16, 185, 129, 180));
        bookingsStatLabel = createStatBadge("📋 Booked: 0", new Color(245, 158, 11, 180));

        statsPanel.add(eventsStatLabel);
        statsPanel.add(seatsStatLabel);
        statsPanel.add(bookingsStatLabel);

        add(textPanel, BorderLayout.WEST);
        add(statsPanel, BorderLayout.EAST);
    }

    private JLabel createStatBadge(String initialText, Color bg) {
        JLabel label = new JLabel(initialText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(6, 14, 6, 14));
        label.setOpaque(false);
        return label;
    }

    public void updateStats(int eventCount, int availableSeats, int bookingCount) {
        eventsStatLabel.setText("🎪 Events: " + eventCount);
        seatsStatLabel.setText("🎫 Seats: " + availableSeats);
        bookingsStatLabel.setText("📋 Booked: " + bookingCount);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // Diagonal gradient from Indigo to Purple
        GradientPaint gp = new GradientPaint(0, 0, UITheme.GRADIENT_START, getWidth(), getHeight(), UITheme.GRADIENT_END);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
