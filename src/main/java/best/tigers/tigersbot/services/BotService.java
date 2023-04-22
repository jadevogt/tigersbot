package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.factories.AbstractHandlerFactory;
import best.tigers.tigersbot.util.Environment;
import best.tigers.tigersbot.util.Log;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class BotService {
    private static BotService instance;
    private final TelegramBot bot;
    private final WeakHashMap<Long, List<MessageHandler>> builtHandlers = new WeakHashMap<>();
    private final List<AbstractHandlerFactory> handlerFactories = new ArrayList<>();

    private BotService() throws MissingEnvironmentVariableException {
        var telegramBotToken = Environment.get("TG_BOT_TOKEN");
        bot = new TelegramBot(telegramBotToken);
        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                processUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        Message message = update.message();
        if (message == null || message.text() == null) {
            return;
        }
        if (message.text().equals("/help")) {
            sendHelpMessage(message);
            return;
        }
        getHandlers(message).stream().filter(h -> h.invokationTest(message)).forEach(h -> h.handle(message));
    }

    private List<MessageHandler> getHandlers(Message message) {
        if (builtHandlers.containsKey(message.chat().id())) {
            return builtHandlers.get(message.chat().id());
        }
        var handlerList = new ArrayList<MessageHandler>();
        for (var factory : handlerFactories) {
            try {
                var handler = factory.build(message.chat().id());
                handlerList.add(handler);
            } catch (MissingEnvironmentVariableException e) {
                Log.severe(e.getMessage());
            }
        }
        builtHandlers.put(message.chat().id(), handlerList);
        return handlerList;
    }

    private void sendHelpMessage(Message message) {
        var helpBuilder = new StringBuilder();
        helpBuilder.append("Available commands:");
        handlerFactories.forEach(f -> {
            helpBuilder.append("\n").append(f.getHelp());
        });
        var msg = new SendMessage(message.chat().id(), helpBuilder.toString());
        bot.execute(msg);
    }

    public void registerHandlerFactory(AbstractHandlerFactory factory) {
        factory.setTelegramBot(bot);
        handlerFactories.add(factory);
    }

    public static BotService getInstance() throws MissingEnvironmentVariableException {
        if (instance == null) {
            instance = new BotService();
        }
        return instance;
    }

}
