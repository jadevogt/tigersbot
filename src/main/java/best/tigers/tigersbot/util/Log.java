package best.tigers.tigersbot.util;

import java.util.Arrays;
import java.util.logging.Logger;

public class Log {
    static final Logger logger = Logger.getLogger(Log.class.getName());
    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static boolean checkEnvironmentVariables(String ownerName, String... envVars) {
        var env = System.getenv();
        var missing = Arrays.stream(envVars).filter(v -> env.get(v) == null).toList();
        if (missing.size() == 0) {
            return true;
        }
        var errorBuilder = new StringBuilder();
        errorBuilder.append("Missing environment variables for ")
                .append(ownerName)
                .append(":");
        missing.forEach(m -> errorBuilder.append("\n\t").append(m));
        Log.severe(errorBuilder.toString());
        return false;
    }
}
