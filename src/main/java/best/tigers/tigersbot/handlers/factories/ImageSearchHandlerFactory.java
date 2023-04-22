package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.ImageSearchHandler;
import best.tigers.tigersbot.handlers.MessageHandler;
import com.pengrad.telegrambot.TelegramBot;

public class ImageSearchHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new ImageSearchHandler(getTelegramBot(), chatId);
    }

    public String getHelp() {
        return "/image [query]";
    }
}
