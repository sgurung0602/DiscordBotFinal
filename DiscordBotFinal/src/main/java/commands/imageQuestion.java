//package commands;
//
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.components.ActionRow;
//import net.dv8tion.jda.api.interactions.components.buttons.Button;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class imageQuestion extends ListenerAdapter {
//
//    private final ScoreTracker scoreTracker;
//    private final Map<String, String> currentImageAnswerMap = new HashMap<>();
//
//    public imageQuestion(ScoreTracker scoreTracker) {
//        this.scoreTracker = scoreTracker;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        if (event.getName().equals("imagequestion")) {
//            execute(event);
//        }
//        else {
//            return;
//        }
//    }
//
//    public void execute(SlashCommandInteractionEvent event) {
//        String userId = event.getUser().getId();
//        EmbedBuilder embedBuilder = new EmbedBuilder();
//        embedBuilder.setTitle("What is this University called?");
//        embedBuilder.setDescription("University of Kent? or University of Canterbury");
//        embedBuilder.setImage("https://www.canterburybid.co.uk/wp-content/uploads/2018/07/UKC-logo.jpg");
//        embedBuilder.setAuthor("Test from bipin");
//
//        Button universityButton = Button.primary("UKC", "University of Kent");
//        Button canterburyButton = Button.danger("UKCC", "University of Canterbury");
//
//        currentImageAnswerMap.put(userId, "University of Kent");
//
//        event.replyEmbeds(embedBuilder.build())
//                .addActionRow(universityButton, canterburyButton)
//                .queue();
//    }
//
//    @Override
//    public void onButtonInteraction(ButtonInteractionEvent event) {
//        String userId = event.getUser().getId();
//        if (!currentImageAnswerMap.containsKey(userId)) return;
//
//        String correctAnswer = currentImageAnswerMap.get(userId);
//        String userAnswer = event.getButton().getLabel();
//
//        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
//            scoreTracker.incrementScore(userId);
//            event.reply("Correct! Your score: " + scoreTracker.getScore(userId)).queue();
//        } else {
//            event.reply("Incorrect. Try again!").queue();
//        }
//
//        currentImageAnswerMap.remove(userId);
//    }
//}
