import java.util.*;
import java.io.*;

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

    public void setAvailability(String type, int count) {
        availability.put(type, count);
    }
}

class FilePersistenceService {

    public void saveInventory(RoomInventory inventory, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Integer> e : inventory.getAvailability().entrySet()) {
                bw.write(e.getKey() + "=" + e.getValue());
                bw.newLine();
            }
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    public void loadInventory(RoomInventory inventory, String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean valid = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    inventory.setAvailability(parts[0], Integer.parseInt(parts[1]));
                    valid = true;
                }
            }

            if (!valid) {
                System.out.println("No valid inventory data found. Starting fresh.");
            }

        } catch (Exception e) {
            System.out.println("Error loading inventory.");
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("System Recovery");

        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistence = new FilePersistenceService();

        String filePath = "inventory.txt";

        persistence.loadInventory(inventory, filePath);

        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> e : inventory.getAvailability().entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        persistence.saveInventory(inventory, filePath);
    }
}