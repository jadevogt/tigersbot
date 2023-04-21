package best.tigers.services;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;

public class CompletionService {
    private static CompletionService instance;
    private final OpenAiService api;

    public static CompletionService getInstance() {
        if (instance == null) {
            instance = new CompletionService();
        }
        return instance;
    }

    private CompletionService() {
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


}
