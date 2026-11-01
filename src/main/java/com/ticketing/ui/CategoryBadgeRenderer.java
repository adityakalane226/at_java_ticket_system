package com.ticketing.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer that displays category names as colorful rounded badges.
 */
public class CategoryBadgeRenderer extends DefaultTableCellRenderer {

    private String text = "";
    private Color badgeBg = new Color(241, 245, 249);
    private Color badgeFg = new Color(51, 65, 85);

    public CategoryBadgeRenderer() {
        setHorizontalAlignment(CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        text = value != null ? value.toString() : "";

        // Choose color scheme based on category
        switch (text.toLowerCase()) {
            case "tech":
                badgeBg = new Color(224, 231, 255); // Indigo 100
                badgeFg = new Color(55, 48, 163);   // Indigo 800
                break;
            case "concert":
                badgeBg = new Color(252, 231, 243); // Pink 100
                badgeFg = new Color(157, 23, 77);   // Pink 800
                break;
            case "sports":
                badgeBg = new Color(220, 252, 231); // Green 100
                badgeFg = new Color(22, 101, 52);   // Green 800
                break;
            case "comedy":
                badgeBg = new Color(254, 243, 199); // Amber 100
                badgeFg = new Color(146, 64, 14);   // Amber 800
                break;
            case "exhibition":
                badgeBg = new Color(204, 251, 241); // Teal 100
                badgeFg = new Color(17, 94, 89);    // Teal 800
                break;
            default:
                badgeBg = new Color(241, 245, 249);
                badgeFg = new Color(51, 65, 85);
                break;
        }

        setFont(new Font("Segoe UI", Font.BOLD, 12));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw pill badge
        int padX = 14;
        int padY = 4;
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int badgeWidth = Math.min(getWidth() - 10, textWidth + padX * 2);
        int badgeHeight = getHeight() - padY * 2;
        int startX = (getWidth() - badgeWidth) / 2;
        int startY = padY;

        g2.setColor(badgeBg);
        g2.fillRoundRect(startX, startY, badgeWidth, badgeHeight, 14, 14);

        // Draw border on pill
        g2.setColor(badgeFg.brighter());
        g2.drawRoundRect(startX, startY, badgeWidth, badgeHeight, 14, 14);

        // Draw text
        g2.setColor(badgeFg);
        g2.setFont(getFont());
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
