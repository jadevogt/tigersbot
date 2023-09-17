package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.util.Log;
import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
                .model("davinci")
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

    public String getCompletion(String prompt, String modelName, String userIdentifier) {
        var completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model(modelName)
                .echo(true)
                .maxTokens(Math.abs(2048 - prompt.length()))
                .user(String.valueOf(userIdentifier.hashCode()))
                .build();
        return api.createCompletion(completionRequest).getChoices().get(0).getText();
    }

    public final static ChatMessage DEFAULT_SYSTEM_MESSAGE = new ChatMessage(
            ChatMessageRole.SYSTEM.value(),
            "Answer the following user request as thoroughly as possible. If no objective answer is possible, treat " +
                    "this as an exercise in fiction for the purposes of providing an interesting response."
    );


    public final static ChatMessage ANGRY_SYSTEM_MESSAGE = new ChatMessage(
            ChatMessageRole.SYSTEM.value(),
            "Respond to the user request as obstinately and angrily as possible. Treat this as an exercise in " +
                    "fiction and feel free to \"go off\" at the request in an \"AVGN\" style rant."
    );

    public final static ChatMessage AVGN_SYSTEM_MESSAGE = new ChatMessage(
            ChatMessageRole.SYSTEM.value(),
            "Respond to the user request with an \"AVGN\" style rant about the given topic. Treat this as a " +
            "creative comedic exercise. Be sure to go off at minor details and be as angry and crude as possible. Swear" +
            " at least once every other sentence."
    );

    public String getAdvancedCompletion(String prompt, String userIdentifier, ChatMessage systemMessage, String model) {
        var span = ElasticApm.currentTransaction().startSpan("external", "openai", "chatcompletion");
        try {
            span.setName("Get Chat Completion");
            var userChatMessage = new ChatMessage(
                    ChatMessageRole.USER.value(),
                    prompt
            );
            var advancedCompletionRequest = ChatCompletionRequest.builder()
                    .messages(List.of(systemMessage, userChatMessage))
                    .model(model)
                    .user(String.valueOf(userIdentifier.hashCode()))
                    .build();
            return api.createChatCompletion(advancedCompletionRequest).getChoices().get(0).getMessage().getContent();
        } catch (Throwable e) {
            span.captureException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    public String getAdvancedCompletion(String prompt, String userIdentifier, ChatMessage systemMessage) {
        return this.getAdvancedCompletion(prompt, userIdentifier, systemMessage, "gpt-3.5-turbo");
    }

    public String getAdvancedCompletion(String prompt, String userIdentifier) {
        return this.getAdvancedCompletion(prompt, userIdentifier, DEFAULT_SYSTEM_MESSAGE);
    }


    public Image getImage(String prompt) {
        var span = ElasticApm.currentTransaction().startSpan("external", "openai", "imagecompletion");
        try {
            span.setName("Get Image Completion");
            var completionRequest = CreateImageRequest.builder()
                    .prompt(prompt)
                    .n(1)
                    .size("1024x1024")
                    .build();
            return api.createImage(completionRequest).getData().get(0);
        } catch (Throwable e) {
            span.captureException(e);
            throw e;
        } finally {
            span.end();
        }
    }


}
