package best.tigers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.function.BiConsumer;

public class TestHandler implements MessageHandler {
    private static TestHandler instance;

    public static TestHandler getInstance() {
        if (instance == null) {
            instance = new TestHandler();
        }
        return instance;
    }

    @Override
    public void handle(TelegramBot bot, Message message) {
        var name = message.from().firstName();

        System.out.println("Invoking test handler");
        var msg = new SendMessage(message.chat().id(), "Test successful, " + name);
        bot.execute(msg);
    }

    public boolean invokationTest(TelegramBot bot, Message message) {
        return (message.text().contains("test"));
    }

    public String getHelp() {
        return "/test";
    }
}
