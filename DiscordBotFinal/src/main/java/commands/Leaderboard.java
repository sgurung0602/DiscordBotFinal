package commands;

import XMLstuff.QuestionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Leaderboard extends ListenerAdapter {
    private final ScoreTracker scoreTracker;
    private final QuestionManager questionManager;
    private final ScheduledExecutorService scheduler;
    private Message currentLeaderboardMessage;

    public Leaderboard(ScoreTracker scoreTracker, QuestionManager questionManager) {
        this.scoreTracker = scoreTracker;
        this.questionManager = questionManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.currentLeaderboardMessage = null;  // Initialize with null, meaning no leaderboard is currently displayed
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("leaderboard")) {
            displayLeaderboard(event);
        } else if (event.getName().equals("increment_score")) {
            incrementUserScore(event);
        }
    }

    private void displayLeaderboard(SlashCommandInteractionEvent event) {
        if (currentLeaderboardMessage != null) {
            event.reply("A leaderboard is already being displayed. Please wait until it disappears.").setEphemeral(true).queue();
            return;
        }

        initializeUserScores(event);

        Map<String, Integer> scores = scoreTracker.getScores();

        if (scores.isEmpty()) {
            event.reply("No scores available right now.").queue();
            return;
        }

        String leaderboard = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> String.format("<@%s>: %d", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Leaderboard");
        embedBuilder.setDescription(leaderboard);
        embedBuilder.setColor(Color.CYAN);

        event.replyEmbeds(embedBuilder.build()).queue(message -> {
            currentLeaderboardMessage = message.retrieveOriginal().complete();

            scheduler.schedule(() -> {
                if (currentLeaderboardMessage != null) {
                    currentLeaderboardMessage.delete().queue();
                    currentLeaderboardMessage = null;  // Reset to allow a new leaderboard to be shown
                }
            }, 2, TimeUnit.MINUTES);
        });
    }

    private void incrementUserScore(SlashCommandInteractionEvent event) {
        User user = event.getOption("user").getAsUser();

        if (user != null && questionManager.isQuizmaster(event.getUser().getId())) {
            scoreTracker.incrementScore(user.getId());
            event.reply(user.getAsMention() + "'s score has been incremented by 1.").queue();
        } else {
            event.reply("Only the quiz master can increment scores.").setEphemeral(true).queue();
        }
    }

    private void initializeUserScores(SlashCommandInteractionEvent event) {
        event.getGuild().getMembers().forEach(member -> {
            if (!member.getUser().isBot()) {
                scoreTracker.getScores().putIfAbsent(member.getId(), 0);
            }
        });
    }
}
