import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 2);
    }

    public Map<String, Integer> getAvailability() {
        return availability;
    }

    public void decrease(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

class RoomAllocationService {
    private Map<String, Integer> counters = new HashMap<>();

    public void allocateRoom(Reservation r, RoomInventory inventory) {
        String type = r.getRoomType();
        Map<String, Integer> map = inventory.getAvailability();

        if (map.get(type) > 0) {
            counters.putIfAbsent(type, 0);
            int id = counters.get(type) + 1;
            counters.put(type, id);

            String roomId = type + "-" + id;
            inventory.decrease(type);

            System.out.println("Booking confirmed for Guest: " +
                    r.getGuestName() + ", Room ID: " + roomId);
        }
    }
}

class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService) {

        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    public void run() {

        while (true) {
            Reservation r;

            synchronized (bookingQueue) {
                if (!bookingQueue.hasRequests())
                    break;
                r = bookingQueue.getNextRequest();
            }

            synchronized (inventory) {
                allocationService.allocateRoom(r, inventory);
            }
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println("Remaining Inventory:");
        for (Map.Entry<String, Integer> e : inventory.getAvailability().entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }
}