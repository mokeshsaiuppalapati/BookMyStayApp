import java.util.*;

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void increaseAvailability(String roomType) {
        roomAvailability.put(roomType, roomAvailability.get(roomType) + 1);
    }
}

class CancellationService {
    private Stack<String> releasedRoomIds;
    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {
        releasedRoomIds = new Stack<>();
        reservationRoomTypeMap = new HashMap<>();
    }

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Invalid cancellation request.");
            return;
        }

        String roomType = reservationRoomTypeMap.remove(reservationId);
        releasedRoomIds.push(reservationId);
        inventory.increaseAvailability(roomType);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    public void showRollbackHistory() {
        System.out.println("Rollback History (Most Recent First):");
        Stack<String> temp = (Stack<String>) releasedRoomIds.clone();
        while (!temp.isEmpty()) {
            System.out.println("Released Reservation ID: " + temp.pop());
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Booking Cancellation");

        RoomInventory inventory = new RoomInventory();
        CancellationService cancelService = new CancellationService();

        cancelService.registerBooking("Single-1", "Single");

        cancelService.cancelBooking("Single-1", inventory);

        cancelService.showRollbackHistory();

        System.out.println("Updated Single Room Availability: " +
                inventory.getRoomAvailability().get("Single"));
    }
}