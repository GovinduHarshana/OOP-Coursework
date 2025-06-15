import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SystemConfiguration {

    private int maxTicketCapacity;
    private int totalTickets;
    private double ticketReleaseRate;
    private double customerRetrievalRate;

    // Mark scanner as transient to prevent Gson from attempting to serialize it
    private transient final Scanner scanner = new Scanner(System.in);

    public SystemConfiguration() {
        collectInputs();
    }

    // Getter and setter for maxTicketCapacity
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        if (maxTicketCapacity <= 0) {
            throw new IllegalArgumentException("Maximum ticket capacity must be greater than 0.");
        }
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Getter and setter for totalTickets
    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        if (totalTickets <= 0 || totalTickets > maxTicketCapacity) {
            throw new IllegalArgumentException(
                    "Total tickets must be greater than 0 and less than or equal to maximum capacity."
            );
        }
        this.totalTickets = totalTickets;
    }

    // Getter and setter for ticketReleaseRate
    public double getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(double ticketReleaseRate) {
        if (ticketReleaseRate <= 0) {
            throw new IllegalArgumentException("Ticket release rate must be greater than 0.");
        }
        this.ticketReleaseRate = ticketReleaseRate;
    }

    // Getter and setter for customerRetrievalRate
    public double getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(double customerRetrievalRate) {
        if (customerRetrievalRate <= 0) {
            throw new IllegalArgumentException("Customer retrieval rate must be greater than 0.");
        }
        this.customerRetrievalRate = customerRetrievalRate;
    }

    /**
     * collectInputs method is for Collect and validate user inputs.
     * Throws NumberFormatException for invalid data type.
     * Throws IllegalArgumentException for invalid arguments.
     */
    private void collectInputs() {
        while (true) {
            try {
                System.out.print("Enter maximum ticket capacity: ");
                String input = scanner.nextLine();
                setMaxTicketCapacity(Integer.parseInt(input));

                System.out.print("Enter total tickets to sell: ");
                input = scanner.nextLine();
                setTotalTickets(Integer.parseInt(input));

                System.out.print("Enter ticket release rate (in seconds): ");
                input = scanner.nextLine();
                setTicketReleaseRate(Double.parseDouble(input));

                System.out.print("Enter customer retrieval rate (in seconds): ");
                input = scanner.nextLine();
                setCustomerRetrievalRate(Double.parseDouble(input));

                break;                                                   // Exit loop when all inputs are valid
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter numeric values.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " Please try again.");
            }
        }
    }

    // Save configuration to a file
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
