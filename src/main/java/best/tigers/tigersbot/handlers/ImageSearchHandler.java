package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.ImagesService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendPhoto;

public class ImageSearchHandler extends MessageHandler {
    private final ImagesService imagesService;

    public ImageSearchHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        imagesService = ImagesService.getInstance();
    }

    @Override
    public void handle(Message message) {
        showTypingIndicator();
        var query = message.text().split("/image")[1].strip();
        var image = imagesService.getImage(query);
        sendPhoto(image);
    }

    @Override
    public boolean invokationTest(Message message) {
        return (message.text().startsWith("/image "));
    }

    @Override
    public String getHelp() {
        return "/image [query]";
    }
}
