package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.util.Log;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;

public class CompletionService {
    private static CompletionService instance;
    private final OpenAiService api;

    public static CompletionService getInstance() throws MissingEnvironmentVariableException {
        if (instance == null) {
            instance = new CompletionService();
        }
        return instance;
    }

    private CompletionService() throws MissingEnvironmentVariableException {
        var variablesExist = Log.checkEnvironmentVariables(
                "CompletionService",
                "OPEN_AI_TOKEN");
        if (!variablesExist) {
            throw new MissingEnvironmentVariableException("OPEN_AI_TOKEN");
        }
        var environmentVariables = System.getenv();
        var openAiToken = environmentVariables.get("OPEN_AI_TOKEN");
        api = new OpenAiService(openAiToken, Duration.ofSeconds(999));
    }

    public String getCompletion(String prompt) {
        var completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .echo(true)
                .maxTokens(Math.abs(2048 - prompt.length()))
                .build();
        return api.createCompletion(completionRequest).getChoices().get(0).getText();
    }

    public String getCompletion(String prompt, String modelName) {
        var completionRequest = CompletionRequest.builder()
            .prompt(prompt)
            .model(modelName)
            .echo(true)
            .maxTokens(Math.abs(2048 - prompt.length()))
            .build();
        return api.createCompletion(completionRequest).getChoices().get(0).getText();
    }



    public Image getImage(String prompt) {
        var completionRequest = CreateImageRequest.builder()
                .prompt(prompt)
                .n(1)
                .size("1024x1024")
                .build();
        return api.createImage(completionRequest).getData().get(0);
    }


}
