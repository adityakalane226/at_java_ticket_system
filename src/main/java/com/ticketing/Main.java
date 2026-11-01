package com.ticketing;

import com.ticketing.service.BookingService;

import java.awt.Desktop;
import java.net.URI;

/**
 * Main Entry Point for the Event Ticket Booking Website.
 * Starts the Java HTTP Web Server and automatically launches the website in the default browser.
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   PASSAGE — Event Ticket Booking System (Web Application)     ");
        System.out.println("===============================================================");

        BookingService bookingService = new BookingService();
        WebServer webServer = new WebServer(bookingService, PORT);

        try {
            webServer.start();
            String webUrl = "http://localhost:" + PORT + "/";
            System.out.println(">> Website is live at: " + webUrl);
            System.out.println(">> Press Ctrl + C in this terminal anytime to stop the server.");
            System.out.println("===============================================================");

            // Automatically open default browser
            openBrowser(webUrl);

        } catch (Exception e) {
            System.err.println("Failed to start web server on port " + PORT + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Windows fallback command
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            }
        } catch (Exception ignored) {
            System.out.println("Note: Please open " + url + " manually in your web browser.");
        }
    }
}
