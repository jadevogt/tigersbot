package best.tigers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;

public class SoundHandler implements MessageHandler {
    private static SoundHandler instance;
    private final HashMap<String, String> sounds = new HashMap<>();

    private SoundHandler() {
        var environmentVariables = System.getenv();
        // Sounds can be loaded in by putting them in your environment vars in the format SOUND_SOUND_NAME
        // underscores in the name will be replaced with spaces, and the name will be made all lowercase
        for (var key : environmentVariables.keySet()) {
            if (key.startsWith("SOUND_")) {
                var soundUrl = environmentVariables.get(key).replace("\"", "");
                var soundName = key.split("SOUND_")[1].toLowerCase().replace("_", " ");
                sounds.put(soundName, soundUrl);
            }
        }
    }

    public static SoundHandler getInstance() {
        if (instance == null) {
            instance = new SoundHandler();
        }
        return instance;
    }

    @Override
    public void handle(TelegramBot bot, Message message) {
        if (message.text().strip().equals("/sound")) {
            StringBuilder soundList = new StringBuilder();
            soundList.append("Available sounds:");
            sounds.keySet().forEach(s -> soundList.append("\n").append(s));
            var msg = new SendMessage(message.chat().id(), soundList.toString());
            bot.execute(msg);
            return;
        }
        var soundName = message.text().split("/sound")[1].strip();
        if (!sounds.containsKey(soundName)) {
            var msg = new SendMessage(message.chat().id(), "\"" + soundName + "\" is not a known sound.");
            bot.execute(msg);
        } else {
            var audioMsg = new SendAudio(message.chat().id(), sounds.get(soundName));
            audioMsg.title(soundName);
            audioMsg.performer("");
            audioMsg.caption("");
            bot.execute(audioMsg);
        }
    }

    @Override
    public boolean invokationTest(TelegramBot bot, Message message) {
        return message.text().startsWith("/sound");
    }

    public String getHelp() {
        return "/sound [soundname]";
    }
}
