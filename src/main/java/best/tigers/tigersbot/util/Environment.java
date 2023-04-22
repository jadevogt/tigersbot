package best.tigers.tigersbot.util;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;

import java.util.Map;

public class Environment {
    public static Map<String, String> environment = System.getenv();

    public static String get(String variable) throws MissingEnvironmentVariableException {
        var val = environment.get(variable);
        if (val == null) {
            throw new MissingEnvironmentVariableException(variable);
        }
        return val;
    }
}
