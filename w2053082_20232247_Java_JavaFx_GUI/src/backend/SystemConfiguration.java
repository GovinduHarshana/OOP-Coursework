package backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class SystemConfiguration {
    private int maxTicketCapacity;
    private int totalTickets;
    private double ticketReleaseRate;
    private double customerRetrievalRate;

    // Default constructor
    public SystemConfiguration() {
        // Default values
        this.maxTicketCapacity = 100;
        this.totalTickets = 50;
        this.ticketReleaseRate = 2;
        this.customerRetrievalRate = 3;
    }

    // New constructor with explicit parameters
    public SystemConfiguration(int maxTicketCapacity, int totalTickets, double ticketReleaseRate, double customerRetrievalRate) {
        setMaxTicketCapacity(maxTicketCapacity);
        setTotalTickets(totalTickets);
        setTicketReleaseRate(ticketReleaseRate);
        setCustomerRetrievalRate(customerRetrievalRate);
    }

    // Existing setters with validation
    public void setMaxTicketCapacity(int maxTicketCapacity) {
        if (maxTicketCapacity <= 0) {
            throw new IllegalArgumentException("Maximum ticket capacity must be greater than 0.");
        }
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public void setTotalTickets(int totalTickets) {
        if (totalTickets <= 0 || totalTickets > maxTicketCapacity) {
            throw new IllegalArgumentException(
                    "Total tickets must be greater than 0 and less than or equal to maximum capacity."
            );
        }
        this.totalTickets = totalTickets;
    }

    public void setTicketReleaseRate(double ticketReleaseRate) {
        if (ticketReleaseRate <= 0) {
            throw new IllegalArgumentException("Ticket release rate must be greater than 0.");
        }
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public void setCustomerRetrievalRate(double customerRetrievalRate) {
        if (customerRetrievalRate <= 0) {
            throw new IllegalArgumentException("Customer retrieval rate must be greater than 0.");
        }
        this.customerRetrievalRate = customerRetrievalRate;
    }

    // Existing getters
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public double getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public double getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    // Existing JSON methods
    public static SystemConfiguration loadFromFile(String filePath) {
        Gson gson = new GsonBuilder().create();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, SystemConfiguration.class);
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            return new SystemConfiguration(); // Return a default configuration if loading fails
        }
    }

    public void saveToFile(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(this, writer);
            System.out.println("Configuration saved to '" + filePath + "'.");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }
}