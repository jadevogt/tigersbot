package best.tigers.services;

import best.tigers.handlers.MessageHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class BotService {
    private static BotService instance;
    private final TelegramBot bot;

    private final List<MessageHandler> handlers = new ArrayList<>();

    private BotService() {
        var environmentVariables = System.getenv();
        var telegramBotToken = environmentVariables.get("TG_BOT_TOKEN");
        bot = new TelegramBot(telegramBotToken);
        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                if (update.message() == null) {
                    continue;
                }
                var message = update.message();
                var text = message.text();
                if (text == null) {
                    continue;
                }
                if (text.equals("/help")) {
                    StringBuilder commandList = new StringBuilder();
                    commandList.append("Available Commands:");
                    handlers.forEach(h -> commandList.append("\n").append(h.getHelp()));
                    var msg = new SendMessage(message.chat().id(), commandList.toString());
                    bot.execute(msg);
                }
                handlers.forEach(handler -> {
                    if (handler.invokationTest(bot, message)) {
                        handler.handle(bot, message);
                    }
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void registerHandler(MessageHandler handler) {
        handlers.add(handler);
    }

    public void removeHandler(MessageHandler handler) {
        handlers.remove(handler);
    }

    public static BotService getInstance() {
        if (instance == null) {
            instance = new BotService();
        }
        return instance;
    }
}
