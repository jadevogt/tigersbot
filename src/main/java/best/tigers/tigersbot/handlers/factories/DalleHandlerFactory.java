package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.DalleHandler;
import best.tigers.tigersbot.handlers.MessageHandler;

public class DalleHandlerFactory extends AbstractHandlerFactory {

    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new DalleHandler(getTelegramBot(), chatId);
    }

    @Override
    public String getHelp() {
        return "/dalle [prompt]";
    }
}
