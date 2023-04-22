package best.tigers.tigersbot.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public class TestHandler extends MessageHandler {

    public TestHandler(TelegramBot bot, long chatId) {
        super(bot, chatId);
    }

    @Override
    public void handle(Message message) {
        var name = message.from().firstName();
        System.out.println("Invoking test handler");
        sendMessage("Test successful, " + name);
    }

    public boolean invokationTest(Message message) {
        return (message.text().startsWith("/test"));
    }

    public String getHelp() {
        return "/test";
    }
}
