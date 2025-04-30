import XMLstuff.QuestionManager;
import commands.ScoreTracker;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;

public class Listener extends ListenerAdapter {

    private final ScoreTracker scoreTracker;
    private final QuestionManager questionManager;

    public Listener(ScoreTracker scoreTracker, QuestionManager questionManager) {
        this.scoreTracker = scoreTracker;
        this.questionManager = questionManager;
    }

    @Override
    public void onReady(ReadyEvent event) {
        Guild guild = event.getJDA().getGuildById("1247216706766635110");
        if (guild != null) {
            guild.upsertCommand("triviaquestion", "Get a round of 5 random Trivia questions")
                    .addOptions(new OptionData(OptionType.STRING, "topic", "Select a topic", true)
                            .addChoice("Geography", "geography")
                            .addChoice("Games", "games")
                            .addChoice("Sports", "sport")).queue();



            guild.upsertCommand("leaderboard", "Show the leaderboard").queue();
            guild.upsertCommand("increment_score", "Increment a user's score, this can only be accessed by the quizmaster")
                    .addOptions(new OptionData(OptionType.USER, "user", "Select a user", true)).queue();
            guild.upsertCommand("customxml", "Create your own quiz using XML and become a quizmaster")
                    .addOption(OptionType.ATTACHMENT, "file", "Upload an XML file", true).queue();
            guild.upsertCommand("help", "Get information about the bot e.g. how to create custom quizzes and how to increment the score").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("help")) {
            handleHelpCommand(event);
        }
    }

    private void handleHelpCommand(SlashCommandInteractionEvent event) {
        String helpMessage = "To start a quiz, you can select some of the default quizzes made by me, or you can upload your own custom quiz using the attached XML file!\n\n"
                + "PLEASE BE AWARE THAT YOU CAN ONLY USE THE INCREMENT_SCORE COMMAND WHEN YOU ARE A QUIZ MASTER, AND TO BE A QUIZ MASTER YOU HAVE TO UPLOAD YOUR OWN QUIZ.\n\n"
                + "Please find below the custom quiz XML format. Fill in the tags and make your own custom quiz! You can copy and paste the format for as many questions "
                + "as you like.\n\n";


        File xmlFile = new File("C:\\Users\\Bipin Rai\\Desktop\\Format_for_custom_quiz.xml");

        if (xmlFile.exists()) {
            event.reply(helpMessage)
                    .addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(xmlFile))
                    .queue();
        } else {
            event.reply("no XML file found").queue();
        }
    }
}
