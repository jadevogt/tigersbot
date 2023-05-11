package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.CompletionService;
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
        showTypingIndicator();
        var prompt = message.text().split("/gpt")[1].strip();
        var completion = completionService.getCompletion(prompt);
        sendReplyMessage(message, completion);
    }

    @Override
    public boolean invokationTest(Message message) {
        return message.text().startsWith("/gpt ");
    }

    @Override
    public String getHelp() {
        return "/gpt (model=[model name]) [prompt]";
    }
}
