package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.CompletionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.theokanning.openai.image.Image;

public class DalleHandler extends MessageHandler {
    private CompletionService completionService;

    public DalleHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        completionService = CompletionService.getInstance();
    }

    @Override
    public void handle(Message message) {
        var prompt = message.text().split("/dalle")[1].strip();
        showTypingIndicator();
        Image completion = completionService.getImage(prompt);
        sendPhoto(completion.getUrl());
    }

    @Override
    public boolean invokationTest(Message message) {
        return message.text().startsWith("/dalle");
    }

    @Override
    public String getHelp() {
        return "/dalle [prompt]";
    }
}
