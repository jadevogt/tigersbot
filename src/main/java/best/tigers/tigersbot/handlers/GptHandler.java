package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.CompletionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;

public class GptHandler extends MessageHandler {
    private final CompletionService completionService;

    public GptHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        completionService = CompletionService.getInstance();
    }

    @Override
    public void handle(Message message) {
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
        return "/gpt [prompt]";
    }
}
