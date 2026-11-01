package com.ticketing.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Colorful badge renderer for available seats showing availability status.
 */
public class SeatsBadgeRenderer extends DefaultTableCellRenderer {

    private String text = "";
    private Color badgeBg = new Color(220, 252, 231);
    private Color badgeFg = new Color(22, 101, 52);

    public SeatsBadgeRenderer() {
        setHorizontalAlignment(CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        text = value != null ? value.toString() : "0 / 0";

        try {
            String[] parts = text.split("/");
            if (parts.length == 2) {
                int avail = Integer.parseInt(parts[0].trim());
                int total = Integer.parseInt(parts[1].trim());

                if (avail == 0) {
                    badgeBg = new Color(254, 226, 226); // Red 100
                    badgeFg = new Color(185, 28, 28);   // Red 700
                    text = "SOLD OUT";
                } else if (avail <= total * 0.3) {
                    badgeBg = new Color(254, 243, 199); // Amber 100
                    badgeFg = new Color(180, 83, 9);    // Amber 700
                } else {
                    badgeBg = new Color(220, 252, 231); // Green 100
                    badgeFg = new Color(21, 128, 61);   // Green 700
                }
            }
        } catch (Exception ignored) {
            badgeBg = new Color(241, 245, 249);
            badgeFg = new Color(51, 65, 85);
        }

        setFont(new Font("Segoe UI", Font.BOLD, 12));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padX = 12;
        int padY = 4;
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int badgeWidth = Math.min(getWidth() - 8, textWidth + padX * 2);
        int badgeHeight = getHeight() - padY * 2;
        int startX = (getWidth() - badgeWidth) / 2;
        int startY = padY;

        g2.setColor(badgeBg);
        g2.fillRoundRect(startX, startY, badgeWidth, badgeHeight, 12, 12);

        g2.setColor(badgeFg.brighter());
        g2.drawRoundRect(startX, startY, badgeWidth, badgeHeight, 12, 12);

        g2.setColor(badgeFg);
        g2.setFont(getFont());
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
