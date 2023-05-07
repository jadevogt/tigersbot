package best.tigers.tigersbot.services;

import best.tigers.tigersbot.util.Log;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PersistentStorageService {
    private static PersistentStorageService instance;

    public static PersistentStorageService getInstance() {
        if (instance == null) {
            instance = new PersistentStorageService();
        }
        return instance;
    }

    public synchronized JSONObject jsonFromFile(String filePath) {
        JSONObject jsonObject = null;
        try (FileReader reader = new FileReader(filePath)) {
            jsonObject = new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            Log.severe(e.getMessage());
        }
        return jsonObject;
    }

    public synchronized void jsonToFile(String filePath, JSONObject jsonObject) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonObject.toString());
            writer.flush();
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
