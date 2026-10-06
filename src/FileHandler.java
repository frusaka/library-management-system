import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FileHandler class for data persistence
 * Demonstrates file handling operations for saving and loading data
 */
public class FileHandler {  
    // Save to file method for generic LibraryEntinty
    public static boolean save(List<? extends LibraryEntinty> items) {
        if (items.isEmpty()) {
            System.err.println("No items to save.");
            return false;
        }
        String type = items.get(0).getClass().getSimpleName();
        String filePath = "data/"+type.toLowerCase()+"s.txt";
        
        try {
            createDataDirectory();
            FileWriter writer = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            
            for (LibraryEntinty item : items) {
                if (item instanceof LibraryEntinty) {
                    bufferedWriter.write(item.toFileString());
                    bufferedWriter.newLine();
                }
            }
            
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving " + type + " to " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    // Load from file method for generic LibraryEntinty
    public static <T extends LibraryEntinty> List<T> load(Class<T> type) {
        List<T> items = new ArrayList<>();
        String filePath = "data/" + type.getSimpleName().toLowerCase() + "s.txt";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return items; // Return empty list if file doesn't exist
            }
            
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            
            while ((line = bufferedReader.readLine()) != null) {
                T item = null;
                try{
                    item = type.cast(type.getMethod("fromFileString").invoke(null, line));
                } catch (Exception e) {
                    System.err.println("Error creating instance of " + type.getSimpleName() + ": " + e.getMessage());
                    break;
                }
                // T item = type.getMethod("fromFileString").invoke(null);
                // T item = type.cast(type).fromFileString(line);
                if (item != null) {
                    items.add(item);
                }
            }
            
            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
        }
        return items;
    }

    // Create data directory if it doesn't exist
    private static void createDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    // Backup all data
    public static boolean backupData(List<Book> books, List<Member> members, List<Librarian> librarians) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String backupDir = "data/backup_" + timestamp;
            File dir = new File(backupDir);
            dir.mkdirs();
            Map<String, List<?>> dataMap = Map.of(
                "books", books,
                "members", members,
                "librarians", librarians
            );
            for (Map.Entry<String, List<?>> entry : dataMap.entrySet()) {
                String type = entry.getKey();
                List<?> items = entry.getValue();
                FileWriter writer = new FileWriter(backupDir + "/" + type + "_backup.txt");
                BufferedWriter buffer = new BufferedWriter(writer);
                for (Object item : items) {
                    buffer.write(item.toString());
                    buffer.newLine();
                }
                buffer.close();
            }

            
            System.out.println("✅ Data backup created successfully in: " + backupDir);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error creating backup: " + e.getMessage());
            return false;
        }
    }
}
