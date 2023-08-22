package best.tigers.tigersbot.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

public abstract class MessageHandler {
    private final long chatId;
    private final TelegramBot bot;
    abstract public void handle(Message message);
    abstract public boolean invokationTest(Message message);
    abstract public String getHelp();

    public MessageHandler(TelegramBot bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public long getChatId() {
        return chatId;
    }

    void sendMessage(String message) {
        var newMessage = new SendMessage(chatId, message);
        bot.execute(newMessage);
    }

    void sendPhoto(String photo) {
        var newMessage = new SendPhoto(chatId, photo);
        bot.execute(newMessage);
    }

    void sendSound(String sound, String title, String performer, String caption) {
        var newMessage = new SendAudio(chatId, sound);
        newMessage.title(title);
        newMessage.performer(performer);
        newMessage.caption(caption);
        bot.execute(newMessage);
    }

    void sendSound(String sound) {
        sendSound(sound, "", "", "");
    }

    void sendSound(String sound, String title) {
        sendSound(sound, title, "", "");
    }


    SendMessage sendReplyMessage(int messageId, String message) {
        var newMessage = new SendMessage(chatId, message);
        newMessage.replyToMessageId(messageId);
        bot.execute(newMessage);
        return newMessage;
    }

    SendMessage sendReplyMessage(Message originalMessage, String message) {
        return sendReplyMessage(originalMessage.messageId(), message);
    }

    void showTypingIndicator() {
        var indicatorAction = new SendChatAction(chatId, ChatAction.typing);
        bot.execute(indicatorAction);
    }
}
