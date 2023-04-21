package best.tigers.handlers;

import best.tigers.services.ImagesService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendPhoto;

public class ImageSearchHandler implements MessageHandler {
    private static ImageSearchHandler instance;

    public static ImageSearchHandler getInstance() {
        if (instance == null) {
            instance = new ImageSearchHandler();
        }
        return instance;
    }

    @Override
    public void handle(TelegramBot bot, Message message) {
        var x = new SendChatAction(message.chat().id(), ChatAction.typing);
        bot.execute(x);
        var name = message.from().firstName();
        var query = message.text().split("/image")[1].strip();
        var image = ImagesService.getInstance().getImage(query);
        var imageMessage = new SendPhoto(message.chat().id(), image);
        bot.execute(imageMessage);
    }

    public boolean invokationTest(TelegramBot bot, Message message) {
        return (message.text().startsWith("/image "));
    }

    public String getHelp() {
        return "/image [query]";
    }
}
