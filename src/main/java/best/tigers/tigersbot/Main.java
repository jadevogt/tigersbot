package best.tigers.tigersbot;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.handlers.factories.*;
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
        botService.registerHandlerFactory(new DalleHandlerFactory());
        botService.registerHandlerFactory(new QuoteHandlerFactory());
    }
}