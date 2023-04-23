package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.QuoteHandler;

public class QuoteHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new QuoteHandler(getTelegramBot(), chatId);
    }

    @Override
    public String getHelp() {
        return "/vc";
    }
}
