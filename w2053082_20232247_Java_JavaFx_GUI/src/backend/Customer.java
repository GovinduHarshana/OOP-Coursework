package backend;

import java.util.function.Consumer;

public class Customer implements Runnable {
    private TicketPool ticketPool;
    private int customerRetrievalRate; // Frequency of buying tickets
    private int totalTickets;
    private Consumer<String> ticketStatusListener;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int totalTickets) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.totalTickets = totalTickets;
    }

    public void setTicketStatusListener(Consumer<String> listener) {
        this.ticketStatusListener = listener;
    }

    private void notifyStatusListener(String status) {
        if (ticketStatusListener != null) {
            ticketStatusListener.accept(status);
        }
    }

    @Override
    public void run() {
        while (true) {
            Ticket ticket = ticketPool.buyTicket();

            if (ticket == null) {
                break;
            }

            notifyStatusListener("Customer: Ticket purchased - " + ticket);

            try {
                Thread.sleep(customerRetrievalRate * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                notifyStatusListener("Customer thread interrupted: " + e.getMessage());
                break;
            }
        }

    }
}
