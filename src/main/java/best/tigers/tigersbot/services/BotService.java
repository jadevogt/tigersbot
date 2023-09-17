package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.factories.AbstractHandlerFactory;
import best.tigers.tigersbot.util.Environment;
import best.tigers.tigersbot.util.Log;
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Scope;
import co.elastic.apm.api.Transaction;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BotService {
    private static BotService instance;
    private final TelegramBot bot;
    private final Map<Long, List<MessageHandler>> builtHandlers = Collections.synchronizedMap(new WeakHashMap<>());
    private final List<AbstractHandlerFactory> handlerFactories = Collections.synchronizedList(new ArrayList<>());

    private BotService() throws MissingEnvironmentVariableException {
        var telegramBotToken = Environment.get("TG_BOT_TOKEN");
        bot = new TelegramBot(telegramBotToken);
        bot.setUpdatesListener(updates -> {
            try {
                for (var update : updates) {
                    var t = new Thread(() -> processUpdate(update));
                    t.start();
                }
            } catch (Exception e) {
                Log.severe(e.getMessage());
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
        var handlers = getHandlers(message);
        for (var handler : handlers) {
            if (handler.invokationTest(message)) {
                Transaction transaction = ElasticApm.startTransaction();
                try (Scope scope = transaction.activate()){
                    transaction.setName(handler.getClass().getSimpleName());
                    transaction.setType(Transaction.TYPE_REQUEST);
                    transaction.setLabel("username", message.from().username());
                    transaction.setLabel("message_body", message.text());
                    handler.handle(message);
                } catch (Throwable e) {
                    transaction.captureException(e);
                } finally {
                    transaction.end();
                }
            }
        }
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
        helpBuilder.append("<b>Available commands:</b>");
        handlerFactories.forEach(f -> {
            helpBuilder.append("\n").append(f.getHelp());
        });
        helpBuilder.append("\n").append("<b>/help</b>\n<i>show this help message</i>");
        var msg = new SendMessage(message.chat().id(), helpBuilder.toString());
        msg.parseMode(ParseMode.HTML);
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
