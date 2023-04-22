package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import com.pengrad.telegrambot.TelegramBot;

public abstract class AbstractHandlerFactory {
    private TelegramBot telegramBot = null;

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    public void setTelegramBot(TelegramBot bot) {
        telegramBot = bot;
    }

    public abstract MessageHandler build(long chatId) throws MissingEnvironmentVariableException;

    public abstract String getHelp();
}
