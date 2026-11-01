package com.ticketing.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A colorful rounded button with smooth hover and click effects.
 */
public class ModernButton extends JButton {

    private final Color normalColor;
    private final Color hoverColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text, Color normalColor, Color hoverColor) {
        this(text, normalColor, hoverColor, Color.WHITE);
    }

    public ModernButton(String text, Color normalColor, Color hoverColor, Color textColor) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;

        setForeground(textColor);
        setFont(UITheme.FONT_BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(203, 213, 225));
        } else if (isPressed) {
            g2.setColor(hoverColor.darker());
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        // Fill rounded rectangle with 8px radius
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();

        super.paintComponent(g);
    }
}
