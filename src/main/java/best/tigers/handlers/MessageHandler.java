package best.tigers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MessageHandler {
    void handle(TelegramBot bot, Message message);
    boolean invokationTest(TelegramBot bot, Message message);

    String getHelp();
}
