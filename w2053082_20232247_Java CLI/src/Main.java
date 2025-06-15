public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Real-Time Event Ticketing System!");

        // Configure the system
        SystemConfiguration config = new SystemConfiguration();
        config.saveToFile("systemConfig.json");

        // Create the TicketPool
        TicketPool ticketPool = new TicketPool(config.getMaxTicketCapacity());
        ticketPool.initialize(config.getTotalTickets());

        // Vendor threads
        int totalTickets = config.getTotalTickets();
        int numberOfVendors = 3;
        double ticketReleaseRate = config.getTicketReleaseRate();

        Thread[] vendorThreads = new Thread[numberOfVendors];
        for (int i = 0; i < numberOfVendors; i++) {
            Vendor vendor = new Vendor(ticketPool, (int) ticketReleaseRate);
            Thread vendorThread = new Thread(vendor, "Vendor ID-" + (i + 1));
            vendorThreads[i] = vendorThread;
            vendorThread.start();
        }

        // Customer threads
        int numberOfCustomers = 4;
        double customerRetrievalRate = config.getCustomerRetrievalRate();

        Thread[] customerThreads = new Thread[numberOfCustomers];
        for (int i = 0; i < numberOfCustomers; i++) {
            Customer customer = new Customer(ticketPool, (int) customerRetrievalRate, totalTickets);
            Thread customerThread = new Thread(customer, "Customer ID-" + (i + 1));
            customerThreads[i] = customerThread;
            customerThread.start();
        }
        System.out.println("System is running! Vendors and customers are interacting with the ticket pool.\n");

        //main thread wait for all threads to complete
        try {
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
            }
            for (Thread customerThread : customerThreads) {
                customerThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted: " + e.getMessage());
        }

        System.out.println("\nAll Total tickets have been sold out.");
    }
}
