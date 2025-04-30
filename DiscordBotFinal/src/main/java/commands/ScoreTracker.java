package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ScoreTracker extends ListenerAdapter {
    protected final Map<String, Integer> userScores;

    public ScoreTracker() {
        this.userScores = new HashMap<>();
    }

    public void incrementScore(String userId) {
        userScores.put(userId, userScores.getOrDefault(userId, 0) + 1);
    }

    public int getScore(String userId) {
        return userScores.getOrDefault(userId, 0);
    }

    public Map<String, Integer> getScores() {
        return userScores;
    }
}
