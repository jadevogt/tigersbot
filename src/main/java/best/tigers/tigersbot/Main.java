package best.tigers.tigersbot;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.factories.ForbiddenWordHandlerFactory;
import best.tigers.tigersbot.handlers.factories.GptHandlerFactory;
import best.tigers.tigersbot.handlers.factories.ImageSearchHandlerFactory;
import best.tigers.tigersbot.handlers.factories.SoundHandlerFactory;
import best.tigers.tigersbot.services.BotService;
import best.tigers.tigersbot.util.Log;

public class Main {
    public static void main(String[] args) {
        BotService botService = null;
        try {
            botService = BotService.getInstance();
        } catch (MissingEnvironmentVariableException e) {
            Log.severe("Could not initialize the telegram bot service: " + "\n\t" + e.getMessage());
            System.exit(1);
        }

        botService.registerHandlerFactory(new ForbiddenWordHandlerFactory());
        botService.registerHandlerFactory(new GptHandlerFactory());
        botService.registerHandlerFactory(new SoundHandlerFactory());
        botService.registerHandlerFactory(new ImageSearchHandlerFactory());
    }
}