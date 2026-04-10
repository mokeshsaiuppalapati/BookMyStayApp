import java.io.*;
import java.util.*;

/**
 * Use Case 12: Data Persistence & System Recovery
 * This class demonstrates saving and restoring system state using Serialization.
 */
class BookingState implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String, Integer> inventory;
    List<String> bookingHistory;

    public BookingState(Map<String, Integer> inventory, List<String> bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }
}

public class BookMyStayApp {
    private static final String STORAGE_FILE = "system_state.ser";
    private Map<String, Integer> inventory = new HashMap<>();
    private List<String> bookingHistory = new ArrayList<>();

    public static void main(String[] args) {
        BookMyStayApp app = new BookMyStayApp();
        app.startSystem();
    }

    public void startSystem() {
        System.out.println("--- System Initializing ---");
        loadState();

        // Simulate some operations if the system is fresh
        if (inventory.isEmpty()) {
            System.out.println("No previous state found. Initializing default inventory...");
            inventory.put("Deluxe Room", 10);
            inventory.put("Suite", 5);
        }

        displayStatus();

        // Simulate a new booking
        performBooking("Deluxe Room", "Alice");

        // Prepare for shutdown
        shutdownSystem();
    }

    private void performBooking(String roomType, String guestName) {
        if (inventory.getOrDefault(roomType, 0) > 0) {
            inventory.put(roomType, inventory.get(roomType) - 1);
            bookingHistory.add("Guest: " + guestName + " booked " + roomType);
            System.out.println("Booking successful for " + guestName);
        } else {
            System.out.println("Booking failed: " + roomType + " is full.");
        }
    }

    private void displayStatus() {
        System.out.println("\n--- Current System State ---");
        System.out.println("Inventory: " + inventory);
        System.out.println("Booking History: " + bookingHistory);
        System.out.println("---------------------------\n");
    }

    // --- Persistence Logic ---

    private void loadState() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            System.out.println("Storage file not found. Starting with fresh state.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(STORAGE_FILE))) {
            BookingState state = (BookingState) in.readObject();
            this.inventory = state.inventory;
            this.bookingHistory = state.bookingHistory;
            System.out.println("System state successfully restored from " + STORAGE_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error recovering data: " + e.getMessage());
            System.out.println("Proceeding with default state (Failure Tolerance).");
        }
    }

    private void shutdownSystem() {
        System.out.println("\n--- System Shutting Down ---");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
            BookingState stateToSave = new BookingState(inventory, bookingHistory);
            out.writeObject(stateToSave);
            System.out.println("Critical state serialized and saved to " + STORAGE_FILE);
        } catch (IOException e) {
            System.err.println("Failed to persist state: " + e.getMessage());
        }
        System.out.println("Shutdown complete.");
    }
}