package best.tigers.tigersbot.handlers;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.services.PersistentStorageService;
import best.tigers.tigersbot.util.Environment;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForbiddenWordHandler extends MessageHandler {
    private final List<String> bannedWords;
    private final ConcurrentHashMap<String, HashMap<Long, Integer>> scoreBoard;
    private final PersistentStorageService persistentStorageService;
    private final String wordsFile;
    private final String scoreFile;

    public ForbiddenWordHandler(TelegramBot bot, long chatId) throws MissingEnvironmentVariableException {
        super(bot, chatId);
        wordsFile = Environment.get("FORBIDDEN_WORDS_FILE");
        scoreFile = Environment.get("FORBIDDEN_SCOREBOARD_FILE");
        persistentStorageService = PersistentStorageService.getInstance();
        bannedWords = Collections.synchronizedList(new ArrayList<>());
        loadWordsFile();
        scoreBoard = new ConcurrentHashMap<>();
        loadScoreFile();
    }

    private void loadWordsFile() {
        var storedJson = persistentStorageService.jsonFromFile(wordsFile);
        if (storedJson == null) {
            return;
        }
        if (storedJson.has(Long.toString(getChatId()))) {
            var storedWords = storedJson
                    .getJSONArray(Long.toString(getChatId()))
                    .toList()
                    .stream()
                    .map(Object::toString)
                    .toList();
            bannedWords.addAll(storedWords);
        }
    }

    private void loadScoreFile() {
        var storedJson = persistentStorageService.jsonFromFile(scoreFile);
        if (storedJson == null) {
            return;
        }
        if (storedJson.has(Long.toString(getChatId()))) {
            var storedScores = storedJson.getJSONObject(Long.toString(getChatId()));
            for (var word : storedScores.keySet()) {
                scoreBoard.put(word, new HashMap<>());
                var storedWordScores = storedScores.getJSONObject(word);
                for (var user : storedWordScores.keySet()) {
                    var storedUser = Long.parseLong(user);
                    var storedScore = storedWordScores.getInt(user);
                    scoreBoard.get(word).put(storedUser, storedScore);
                }
            }
        }
    }

    private void saveScoreFile() {
        var storedJson = persistentStorageService.jsonFromFile(scoreFile);
        if (storedJson == null) {
            storedJson = new JSONObject();
        }
        var jsonOut = new JSONObject();
        for (var word : scoreBoard.keySet()) {
            var wordJsonOut = new JSONObject();
            for (var user : scoreBoard.get(word).keySet()) {
                wordJsonOut.put(Long.toString(getChatId()), scoreBoard.get(word).get(user));
            }
            jsonOut.put(word, wordJsonOut);
        }
        storedJson.put(Long.toString(getChatId()), jsonOut);
        persistentStorageService.jsonToFile(scoreFile, storedJson);
    }

    private int incrementScoreForUser(long userId, String word) {
        int score = 0;
        if (!scoreBoard.containsKey(word)) {
            scoreBoard.put(word, new HashMap<>());
        }
        var wordBoard = scoreBoard.get(word);
        if (!wordBoard.containsKey(userId)) {
            wordBoard.put(userId, 1);
            score = 1;
        } else {
            var currentScore = wordBoard.get(userId);
            wordBoard.put(userId, currentScore+1);
            score = currentScore + 1;
        }
        saveScoreFile();
        return score;
    }

    private void deleteWord(String word) {
        bannedWords.remove(word);
        scoreBoard.remove(word);
        saveWordList();
        saveScoreFile();
    }

    private void saveWordList() {
        var storedJson = persistentStorageService.jsonFromFile(wordsFile);
        if (storedJson == null) {
            storedJson = new JSONObject();
        }
        var storedWordsArray = new JSONArray();
        bannedWords.forEach(storedWordsArray::put);
        storedJson.put(Long.toString(getChatId()), storedWordsArray);
        persistentStorageService.jsonToFile(wordsFile, storedJson);
    }

    @Override
    public void handle(Message message) {
        if (message.text().startsWith("/forbid ")) {
            var cleanedMsg = message.text().replace("/forbid", "").strip().split(" ")[0];
            if (!bannedWords.contains(cleanedMsg)) {
                bannedWords.add(cleanedMsg);
                sendMessage("\"" + cleanedMsg + "\" has been forbidden!");
                saveWordList();
            } else {
                sendMessage("\"" + cleanedMsg + "\" is already forbidden!");
            }
        } else if (message.text().startsWith("/unforbid ")) {
            var cleanedMsg = message.text().replace("/unforbid", "").strip().split(" ")[0];
            if (bannedWords.contains(cleanedMsg)) {
                deleteWord(cleanedMsg);
                sendMessage("\"" + cleanedMsg + "\" is no longer forbidden.");
                saveWordList();
            } else {
                sendMessage("\"" + cleanedMsg + "\" wasn't forbidden anyways.");
            }
        } else {
            for (var word : bannedWords) {
                if (testWord(message.text(), word)) {
                    StringBuilder admonishment = new StringBuilder();
                    var score = incrementScoreForUser(message.from().id(), word);
                    admonishment.append(message.from().firstName())
                            .append(" used the forbidden word \"")
                            .append(word)
                            .append("!\"\nThey've used this word ")
                                    .append(score)
                                            .append(" times.");
                    sendMessage(admonishment.toString());
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
    public boolean invokationTest(Message message) {
        if (message.text().startsWith("/forbid"))
            return true;
        if (message.text().startsWith("/unforbid"))
            return true;
        var detected = bannedWords.stream().filter(w -> testWord(message.text(), w)).toList();
        return detected.size() > 0;
    }

    public String getHelp() {
        return "/forbid [word]\n/unforbid [word]";
    }
}
