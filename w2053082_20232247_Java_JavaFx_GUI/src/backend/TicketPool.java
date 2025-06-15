package backend;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class TicketPool {
    private final int maximumTicketCapacity;
    private final Queue<Ticket> ticketQueue;
    private int remainingTickets;
    private Consumer<String> vendorLogListener;
    private Consumer<String> customerLogListener;
    private Runnable allTicketsSoldOutListener;

    public TicketPool(int maximumTicketCapacity) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.ticketQueue = new LinkedList<>();
        this.remainingTickets = 0;
    }

    // Setters for log listeners
    public void setVendorLogListener(Consumer<String> listener) {
        this.vendorLogListener = listener;
    }

    public void setCustomerLogListener(Consumer<String> listener) {
        this.customerLogListener = listener;
    }

    public void setAllTicketsSoldOutListener(Runnable listener) {
        this.allTicketsSoldOutListener = listener;
    }

    private void logVendorMessage(String message) {
        if (vendorLogListener != null) {
            vendorLogListener.accept(message);
        }
    }

    private void logCustomerMessage(String message) {
        if (customerLogListener != null) {
            customerLogListener.accept(message);
        }
    }

    public void initialize(int totalTickets) {
        this.remainingTickets = totalTickets;
    }

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

            // Log vendor message with current ticket pool status
            logVendorMessage(String.format("%s added ticket | Tickets in pool: %d | Remaining tickets: %d",
                    Thread.currentThread().getName(), ticketQueue.size(), remainingTickets));

            notifyAll();
        }
    }

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
            // Check if all tickets are sold out
            if (remainingTickets == 0 && allTicketsSoldOutListener != null) {
                allTicketsSoldOutListener.run();
            }
            return null;
        }

        Ticket ticket = ticketQueue.poll();

        // Log customer message with ticket details
        logCustomerMessage(String.format("%s bought ticket | Tickets in pool: %d | Ticket: %s",
                Thread.currentThread().getName(), ticketQueue.size(), ticket));

        notifyAll();
        return ticket;
    }

    public synchronized boolean hasTickets() {
        return remainingTickets > 0 || !ticketQueue.isEmpty();
    }

    public synchronized int getRemainingTickets() {
        return remainingTickets;
    }
}