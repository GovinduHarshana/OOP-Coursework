import java.math.BigDecimal;

public class Vendor implements Runnable {
    private static int TicketId = 1;
    private final TicketPool ticketPool;
    private final int ticketReleaseRate;  // Frequency of releasing tickets

    public Vendor(TicketPool ticketPool, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
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
            }

            // Simulate ticket release rate
            try {
                Thread.sleep(ticketReleaseRate * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(Thread.currentThread().getName() + " interrupted: " + e.getMessage());
                break;
            }
        }
    }
}
