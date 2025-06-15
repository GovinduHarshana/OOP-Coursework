package backend;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class Vendor implements Runnable {
    private static int TicketId = 1;
    private final TicketPool ticketPool;
    private final int ticketReleaseRate;  // Frequency of releasing tickets
    private Consumer<String> ticketStatusListener;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public void setTicketStatusListener(Consumer<String> listener) {
        this.ticketStatusListener = listener;
    }

    private void notifyStatusListener(String status) {
        if (ticketStatusListener != null) {
            ticketStatusListener.accept(status);
        }
    }

    // To calculate ticketId
    private static synchronized int getNextTicketId() {
        return TicketId++;
    }

    @Override
    public void run() {
        while (ticketPool.hasTickets()) {
            if (ticketPool.hasTickets()) {
                Ticket ticket = new Ticket(getNextTicketId(), "Dream Event", new BigDecimal("1000"));
                ticketPool.addTicket(ticket);
                notifyStatusListener("Vendor: Ticket added - " + ticket);
            }

            try {
                Thread.sleep(ticketReleaseRate * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                notifyStatusListener(Thread.currentThread().getName() + " interrupted: " + e.getMessage());
                break;
            }
        }
    }
}
