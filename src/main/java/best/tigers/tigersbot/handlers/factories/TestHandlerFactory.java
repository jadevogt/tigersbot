package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.TestHandler;
import com.pengrad.telegrambot.TelegramBot;

public class TestHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new TestHandler(getTelegramBot(), chatId);
    }

    public String getHelp() {
        return "/help";
    }
}
