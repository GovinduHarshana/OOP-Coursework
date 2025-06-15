public class Customer implements Runnable {
    private TicketPool ticketPool;
    private int customerRetrievalRate; // Frequency of buying tickets
    private int totalTickets;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int totalTickets) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.totalTickets = totalTickets;
    }

    @Override
    public void run() {
        while (true) {
            // Attempt to buy a ticket
            Ticket ticket = ticketPool.buyTicket(); // Assume buyTicket is thread-safe

            // No tickets available; exit the loop
            if (ticket == null) {
                break;
            }

            // Simulate ticket retrieval rate
            try {
                Thread.sleep(customerRetrievalRate * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Customer thread interrupted: " + e.getMessage());
                break;
            }
        }

    }
}
