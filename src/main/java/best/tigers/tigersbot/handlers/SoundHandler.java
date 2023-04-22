package best.tigers.tigersbot.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;

public class SoundHandler extends MessageHandler {
    private final HashMap<String, String> sounds = new HashMap<>();

    public SoundHandler(TelegramBot bot, long chatId) {
        super(bot, chatId);
        var environmentVariables = System.getenv();
        // Sounds can be loaded in by putting them in your environment vars in the format SOUND_SOUND_NAME
        // underscores in the name will be replaced with spaces, and the name will be made all lowercase
        for (var key : environmentVariables.keySet()) {
            if (key.startsWith("SOUND_")) {
                var soundUrl = environmentVariables.get(key);
                var soundName = key.split("SOUND_")[1].toLowerCase().replace("_", " ");
                sounds.put(soundName, soundUrl);
            }
        }
    }

    @Override
    public void handle(Message message) {
        if (message.text().strip().equals("/sound")) {
            StringBuilder soundList = new StringBuilder();
            soundList.append("Available sounds:");
            sounds.keySet().forEach(s -> soundList.append("\n").append(s));
            sendMessage(soundList.toString());
            return;
        }
        var soundName = message.text().split("/sound")[1].strip();
        if (!sounds.containsKey(soundName)) {
            sendMessage("\"" + soundName + "\" is not a known sound.");
        } else {
            sendSound(sounds.get(soundName), soundName);
        }
    }

    @Override
    public boolean invokationTest(Message message) {
        return message.text().startsWith("/sound");
    }

    public String getHelp() {
        return "/sound [soundname]";
    }
}
