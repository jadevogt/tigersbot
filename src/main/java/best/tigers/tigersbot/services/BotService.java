package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.factories.AbstractHandlerFactory;
import best.tigers.tigersbot.util.Log;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class BotService {
    private static BotService instance;
    private final TelegramBot bot;

    private final WeakHashMap<Long, List<MessageHandler>> builtHandlers = new WeakHashMap<>();

    private final List<AbstractHandlerFactory> handlerFactories = new ArrayList<>();


    private BotService() throws MissingEnvironmentVariableException {
        var variablesExist = Log.checkEnvironmentVariables(
                "BotService",
                "TG_BOT_TOKEN");
        if (!variablesExist) {
            throw new MissingEnvironmentVariableException("TG_BOT_TOKEN");
        }
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
                    var helpBuilder = new StringBuilder();
                    helpBuilder.append("Available commands:");
                    handlerFactories.forEach(f -> {
                        helpBuilder.append("\n").append(f.getHelp());
                    });
                    var msg = new SendMessage(message.chat().id(), helpBuilder.toString());
                    bot.execute(msg);
                }
                if (builtHandlers.containsKey(message.chat().id())) {
                    var handlerList = builtHandlers.get(message.chat().id());
                    for (var handler : handlerList) {
                        if (handler.invokationTest(message)) {
                            handler.handle(message);
                        }
                    }
                } else {
                    var handlerList = new ArrayList<MessageHandler>();
                    for (var factory : handlerFactories) {
                        try {
                            var handler = factory.build(message.chat().id());
                            handlerList.add(handler);
                            if (handler.invokationTest(message)) {
                                handler.handle(message);
                            }
                        } catch (MissingEnvironmentVariableException e) {
                            Log.severe(e.getMessage());
                        }
                    }
                    builtHandlers.put(message.chat().id(), handlerList);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
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
