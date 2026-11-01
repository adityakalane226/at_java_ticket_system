package com.ticketing.ui;

import java.awt.*;

/**
 * Visual design system and color palette for the colorful UI.
 */
public final class UITheme {

    private UITheme() {}

    // Brand & Header Colors
    public static final Color GRADIENT_START = new Color(67, 56, 202);    // Indigo 700
    public static final Color GRADIENT_END   = new Color(124, 58, 237);   // Purple 600

    // Primary Accents
    public static final Color PRIMARY        = new Color(79, 70, 229);    // Indigo 600
    public static final Color PRIMARY_HOVER  = new Color(67, 56, 202);    // Indigo 700
    public static final Color SUCCESS        = new Color(16, 185, 129);   // Emerald 500
    public static final Color SUCCESS_HOVER  = new Color(5, 150, 105);    // Emerald 600
    public static final Color DANGER         = new Color(239, 68, 68);    // Rose 500
    public static final Color DANGER_HOVER   = new Color(220, 38, 38);    // Rose 600
    public static final Color INFO           = new Color(14, 165, 233);   // Sky 500
    public static final Color INFO_HOVER     = new Color(2, 132, 199);    // Sky 600

    // Canvas & Cards
    public static final Color CANVAS_BG      = new Color(245, 247, 251);  // Soft slate blue
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(226, 232, 240);  // Slate 200
    public static final Color CARD_BORDER    = new Color(203, 213, 225);  // Slate 300

    // Text Colors
    public static final Color TEXT_MAIN      = new Color(15, 23, 42);     // Slate 900
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);  // Slate 500

    // Fonts
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_SUB   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION      = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD         = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_PRICE        = new Font("Segoe UI", Font.BOLD, 18);
}
