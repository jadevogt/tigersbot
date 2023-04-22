package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.ForbiddenWordHandler;
import best.tigers.tigersbot.handlers.MessageHandler;

public class ForbiddenWordHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new ForbiddenWordHandler(getTelegramBot(), chatId);
    }

    public String getHelp() {
        return "/forbid [word]\n/unforbid [word]";
    }
}
