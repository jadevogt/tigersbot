package best.tigers.tigersbot.handlers.factories;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.MessageHandler;
import best.tigers.tigersbot.handlers.SoundHandler;
import com.pengrad.telegrambot.TelegramBot;

public class SoundHandlerFactory extends AbstractHandlerFactory {
    @Override
    public MessageHandler build(long chatId) throws MissingEnvironmentVariableException {
        return new SoundHandler(getTelegramBot(), chatId);
    }

    public String getHelp() {
        return "<b>/sound</b> [soundname]\n<i>send a sound from the available sound list. leave out the argument to see the sound list</i>";
    }
}
