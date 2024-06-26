package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.CompletionService;
import co.elastic.apm.api.ElasticApm;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public class GptHandler extends MessageHandler {
    private final CompletionService completionService;

    public GptHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        completionService = CompletionService.getInstance();
    }

    @Override
    public void handle(Message message) {
        if (message.text().startsWith("/gpt model=")) {
            showTypingIndicator();
            var input = message.text().split("/gpt model=")[1].strip();
            var modelName = input.split(" ")[0].strip();
            var prompt = input.split(" ")[1].strip();
            var completion = completionService.getCompletion(prompt, modelName);
            sendReplyMessage(message, completion);
            return;
        }
        if (message.text().startsWith("/gpt4 ")) {
            showTypingIndicator();
            var prompt = message.text().split("/gpt4 ")[1].strip();
            var completion = completionService.getAdvancedCompletion(prompt, message.from().username());
            sendReplyMessage(message, completion);
            return;
        }
        if (message.text().startsWith("/pissed ")) {
            showTypingIndicator();
            var prompt = message.text().split("/pissed ")[1].strip();
            var completion = completionService.getAdvancedCompletion(
                    prompt,
                    CompletionService.ANGRY_SYSTEM_MESSAGE
            );
            sendReplyMessage(message, completion);
            return;
        }
        if (message.text().startsWith("/avgn ")) {
            showTypingIndicator();
            var prompt = message.text().split("/avgn ")[1].strip();
            var completion = completionService.getAdvancedCompletion(
                    prompt,
                    CompletionService.AVGN_SYSTEM_MESSAGE
            );
            sendReplyMessage(message, completion);
            return;
        }
        if (message.text().startsWith("/avgn4 ")) {
            showTypingIndicator();
            var prompt = message.text().split("/avgn ")[1].strip();
            var completion = completionService.getAdvancedCompletion(
                    prompt,
                    CompletionService.AVGN_SYSTEM_MESSAGE
            );
            sendReplyMessage(message, completion);
            return;
        }
        showTypingIndicator();
        var prompt = message.text().split("/gpt")[1].strip();
        var completion = completionService.getCompletion(prompt, message.from().username());
        sendReplyMessage(message, completion);
    }

    @Override
    public boolean invokationTest(Message message) {
        return (message.text().startsWith("/gpt") ||
                message.text().startsWith("/pissed") ||
                message.text().startsWith("/avgn") ||
                message.text().startsWith("/avgn4"));
    }

    @Override
    public String getHelp() {
        return "/gpt(4) [prompt]";
    }
}
