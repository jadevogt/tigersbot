package best.tigers.tigersbot.services;

import best.tigers.tigersbot.util.Log;
import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
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
        var span = ElasticApm.currentTransaction().startSpan("io", "filesystem", "read");
        try {
            span.setName("Get JSON from File");
            span.setLabel("path", filePath);
            JSONObject jsonObject = null;
            try (FileReader reader = new FileReader(filePath)) {
                jsonObject = new JSONObject(new JSONTokener(reader));
            } catch (IOException e) {
                Log.severe(e.getMessage());
            }
            return jsonObject;
        } catch (Throwable e) {
            span.captureException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    public synchronized void jsonToFile(String filePath, JSONObject jsonObject) {
        var span = ElasticApm.currentTransaction().startSpan("io", "filesystem", "write");
        try {
            span.setName("Write JSON to File");
            span.setLabel("path", filePath);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(jsonObject.toString());
                writer.flush();
            } catch (Exception e) {
                Log.severe(e.getMessage());
            }
        } catch (Throwable e) {
            span.captureException(e);
            throw e;
        } finally {
            span.end();
        }
    }

}
