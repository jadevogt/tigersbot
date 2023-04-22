package best.tigers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ForbiddenWordHandler implements MessageHandler {
    private static ForbiddenWordHandler instance;
    private final HashMap<Long, List<String>> bannedWords;

    private ForbiddenWordHandler() {
        bannedWords = new HashMap<>();
        try {
            var environmentVariables = System.getenv();
            var forbiddenWordsPath = environmentVariables.get("FORBIDDEN_WORDS_FILE");
            var is = new FileInputStream(forbiddenWordsPath);
            JSONTokener tokener = new JSONTokener(is);
            JSONObject object = new JSONObject(tokener);
            for (var key : object.keySet()) {
                long longKey = Long.parseLong(key);
                JSONArray jsonWordList = object.getJSONArray(key);
                var wordList = new ArrayList<String>();
                jsonWordList.toList().stream().map(Object::toString).forEach(wordList::add);
                bannedWords.put(longKey, wordList);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToJsonFile() {
        try {
            var environmentVariables = System.getenv();
            var forbiddenWordsPath = environmentVariables.get("FORBIDDEN_WORDS_FILE");
            var banned = new JSONObject();
            for (var key: bannedWords.keySet()) {
                var array = new JSONArray();
                array.putAll(bannedWords.get(key));
                banned.put(key.toString(), array);
            }
            var writer = new FileWriter(forbiddenWordsPath);
            writer.write(banned.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ForbiddenWordHandler getInstance() {
        if (instance == null) {
            instance = new ForbiddenWordHandler();
        }
        return instance;
    }

    @Override
    public void handle(TelegramBot bot, Message message) {
        System.out.println("invoking forbidden word handler");
        if (message.text().startsWith("/forbid ")) {
            System.out.println("forbidding new word...");
            var cleanedMsg = message.text().replace("/forbid", "").strip().split(" ")[0];
            if (!bannedWords.containsKey(message.chat().id())) {
                var newList = new ArrayList<String>();
                bannedWords.put(message.chat().id(), newList);
            }
            var chatSpecificList = bannedWords.get(message.chat().id());
            if (!chatSpecificList.contains(cleanedMsg)) {
                chatSpecificList.add(cleanedMsg);
                var msg = new SendMessage(message.chat().id(), "\"" + cleanedMsg + "\" has been forbidden!");
                writeToJsonFile();
                bot.execute(msg);
            } else {
                var msg = new SendMessage(message.chat().id(), "\"" + cleanedMsg + "\" is already forbidden!");
                bot.execute(msg);
            }
        } else if (message.text().startsWith("/unforbid ")) {
            System.out.println("unforbidding word...");
            var cleanedMsg = message.text().replace("/unforbid", "").strip().split(" ")[0];
            if (!bannedWords.containsKey(message.chat().id())) {
                var newList = new ArrayList<String>();
                bannedWords.put(message.chat().id(), new ArrayList<>());
                var msg = new SendMessage(message.chat().id(), "\"" + cleanedMsg + "\" is not forbidden.");
                bot.execute(msg);
                return;
            }
            var chatSpecificList = bannedWords.get(message.chat().id());
            if (chatSpecificList.contains(cleanedMsg)) {
                chatSpecificList.remove(cleanedMsg);
                var msg = new SendMessage(message.chat().id(), "\"" + cleanedMsg + "\" is no longer forbidden.");
                bot.execute(msg);
            } else {
                var msg = new SendMessage(message.chat().id(), "\"" + cleanedMsg + "\" wasn't forbidden anyways.");
                bot.execute(msg);
            }
        } else {
            System.out.println("detected word");
            var chatSpecificList = bannedWords.get(message.chat().id());
            for (var word : chatSpecificList) {
                if (testWord(message.text(), word)) {
                    StringBuilder admonishment = new StringBuilder();
                    admonishment.append(message.from().firstName())
                            .append(" used the forbidden word \"")
                            .append(word)
                            .append("!\" \uD83D\uDC4E\uD83D\uDC4E\uD83D\uDC4E");
                    var msg = new SendMessage(message.chat().id(), admonishment.toString());

                    bot.execute(msg);
                }
            }
        }
    }

    public static boolean testWord(String sentence, String word) {
        String[] words = sentence.split(" ");
        for (String w : words) {
            if (w.equalsIgnoreCase(word)) {
                return true;
            } else if (w.contains(word)) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean invokationTest(TelegramBot bot, Message message) {
        if (message.text().startsWith("/forbid"))
            return true;
        if (message.text().startsWith("/unforbid"))
            return true;
        if (!bannedWords.containsKey(message.chat().id()))
            return false;
        var chatSpecificList = bannedWords.get(message.chat().id());
        for (var word : chatSpecificList) {
            if (testWord(message.text(), word)) {
                System.out.println("detected banned word");
                return true;
            }
        }
        return false;
    }

    public String getHelp() {
        return "/forbid [word]\n/unforbid [word]";
    }
}
