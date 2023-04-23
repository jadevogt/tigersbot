package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.CompletionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.image.Image;
import retrofit2.HttpException;

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
        try {
            Image completion = completionService.getImage(prompt);
            sendPhoto(completion.getUrl());
        } catch (OpenAiHttpException e) {
            sendMessage("The prompt \"" + prompt + "\" triggered OpenAI's content filter. Try rewording it.");
        } catch (HttpException e) {
            sendMessage("We're currently rate-limited by OpenAI, give it a minute.");
        }
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
