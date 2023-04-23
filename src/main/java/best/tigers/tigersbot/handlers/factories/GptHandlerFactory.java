package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.GptHandler;
import best.tigers.tigersbot.handlers.MessageHandler;
import com.pengrad.telegrambot.TelegramBot;

public class GptHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new GptHandler(getTelegramBot(), chatId);
    }

    public String getHelp() {
        return "<b>/gpt</b> [prompt]\n<i>generate text with gpt-3</i>";
    }
}
