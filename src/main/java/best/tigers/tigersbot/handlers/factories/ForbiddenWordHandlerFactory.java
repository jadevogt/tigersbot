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
        return "<b>/forbid</b> [word]\n<i>add a word to the forbidden word list</i>\n<b>/unforbid</b> [word]\n<i>remove a word from the forbidden word list</i>";
    }
}
