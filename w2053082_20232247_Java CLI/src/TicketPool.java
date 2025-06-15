import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maximumTicketCapacity;
    private final Queue<Ticket> ticketQueue;
    private int remainingTickets;

    public TicketPool(int maximumTicketCapacity) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.ticketQueue = new LinkedList<>();
        this.remainingTickets = 0;
    }

    public void initialize(int totalTickets) {
        this.remainingTickets = totalTickets;
    }

    /**
     * Add Ticket method is for Vendor to add tickets.
     * @param ticket
     * Need to synchronized, Because multiple vendors are trying to add ticket to the ticket pool concurrently.
     * if ticket Queue is full. Vendor need to wait.
     * Once a ticket is added to the pool, all waiting threads are notified.
     */
    public synchronized void addTicket(Ticket ticket) {
        while (ticketQueue.size() >= maximumTicketCapacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting to add ticket.", e);
            }
        }

        if (remainingTickets > 0) {
            ticketQueue.add(ticket);
            remainingTickets--;
            notifyAll();
            System.out.println("Ticket added by " + Thread.currentThread().getName() +
                    " | Tickets in pool: " + ticketQueue.size() +
                    " | Remaining tickets: " + remainingTickets);
        }
    }

    /**
     * Remove Ticket method is for Consumer to buy tickets.
     * @return
     * Need to synchronized, Because multiple consumers are trying to buy ticket from the ticket pool concurrently.
     * If ticket Queue is empty. Consumer need to wait.
     * Once a ticket is purchased, all waiting threads are notified.
     */
    public synchronized Ticket buyTicket() {
        while (ticketQueue.isEmpty() && remainingTickets > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting to buy ticket.", e);
            }
        }

        if (ticketQueue.isEmpty()) {
            return null;
        }

        Ticket ticket = ticketQueue.poll();
        notifyAll();
        System.out.println("Ticket bought by " + Thread.currentThread().getName() +
                " , Tickets in pool: " + ticketQueue.size() +
                " , Ticket: " + ticket);
        return ticket;
    }

    public synchronized boolean hasTickets() {
        return remainingTickets > 0 || !ticketQueue.isEmpty();
    }
}
