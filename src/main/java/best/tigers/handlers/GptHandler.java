package best.tigers.handlers;

import best.tigers.services.CompletionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;

public class GptHandler implements MessageHandler {
    private static GptHandler instance;

    private GptHandler() {

    }

    public static GptHandler getInstance() {
        if (instance == null) {
            instance = new GptHandler();
        }
        return instance;
    }

    @Override
    public void handle(TelegramBot bot, Message message) {
        var completionService = CompletionService.getInstance();
        var x = new SendChatAction(message.chat().id(), ChatAction.typing);
        bot.execute(x);
        var prompt = message.text().split("/gpt")[1].strip();
        var completion = completionService.getCompletion(prompt);
        var msg = new SendMessage(message.chat().id(), completion);
        msg.replyToMessageId(message.messageId());
        bot.execute(msg);
    }

    @Override
    public boolean invokationTest(TelegramBot bot, Message message) {
        return message.text().startsWith("/gpt ");
    }

    @Override
    public String getHelp() {
        return "/gpt [prompt]";
    }
}
