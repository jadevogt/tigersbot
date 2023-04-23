package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.PersistentStorageService;
import best.tigers.tigersbot.util.Environment;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import org.json.JSONArray;

import java.util.List;
import java.util.Random;

public class QuoteHandler extends MessageHandler {
    private String quoteFileLocation;
    private PersistentStorageService persistentStorageService;
    private List<String> quotes;
    private Random random;

    public QuoteHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        quoteFileLocation = Environment.get("QUOTE_FILE_LOCATION");
        persistentStorageService = PersistentStorageService.getInstance();
        var storedJson = persistentStorageService.jsonFromFile(quoteFileLocation);
        JSONArray storedQuotes = storedJson.getJSONArray("quotes");
        quotes = storedQuotes.toList().stream().map(Object::toString).toList();
        random = new Random();
    }

    @Override
    public void handle(Message message) {
        var index = random.nextInt(quotes.size());
        var senderName = message.from().firstName();
        sendMessage(quotes.get(index).replace("{player}", senderName.toUpperCase()));
    }

    @Override
    public boolean invokationTest(Message message) {
        return message.text().equals("/vc");
    }

    @Override
    public String getHelp() {
        return "/vc";
    }
}
