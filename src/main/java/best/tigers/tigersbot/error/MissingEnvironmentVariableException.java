package best.tigers.tigersbot.error;

import java.util.List;

public class MissingEnvironmentVariableException extends Exception {
    private final List<String> environmentVariables;

    public MissingEnvironmentVariableException(String... envVars) {
        environmentVariables = List.of(envVars);
    }

    @Override
    public String getMessage() {
        var varListBuilder = new StringBuilder();
        environmentVariables.forEach(v -> varListBuilder.append(v).append(","));
        var varList = varListBuilder.toString();
        if (varList.endsWith(",")) {
            varList = varList.substring(0, varList.length()-1);
        }
        return "Required environment variables were missing: [" + varList + "]";
    }
}
