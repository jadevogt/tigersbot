package best.tigers;

import best.tigers.handlers.*;
import best.tigers.services.BotService;

public class Main {
    public static void main(String[] args) {
        var botService = BotService.getInstance();
        botService.registerHandler(ForbiddenWordHandler.getInstance());
        botService.registerHandler(ImageSearchHandler.getInstance());
        botService.registerHandler(SoundHandler.getInstance());
        botService.registerHandler(GptHandler.getInstance());
    }
}