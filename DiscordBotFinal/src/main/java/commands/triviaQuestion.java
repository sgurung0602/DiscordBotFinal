package commands;

import XMLstuff.QuestionManager;
import XMLstuff.QuestionManager.Question;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class triviaQuestion extends ListenerAdapter {

    private final Map<String, Map<String, Integer>> userTopicQuestionIndexMap = new HashMap<>();
    private final Map<String, String> currentAnswerMap = new HashMap<>();
    private final Map<String, List<Question>> topicQuestionsMap = new HashMap<>();
    private final ScoreTracker scoreTracker;
    private final QuestionManager questionManager;

    public triviaQuestion(ScoreTracker scoreTracker) {
        this.scoreTracker = scoreTracker;
        this.questionManager = new QuestionManager();
        loadAllTopics();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("triviaquestion")) return;

        String userId = event.getUser().getId();
        String selectedTopic = event.getOption("topic").getAsString();

        if (topicQuestionsMap.containsKey(selectedTopic)) {
            // Initialize user's question index for the selected topic
            userTopicQuestionIndexMap.putIfAbsent(userId, new HashMap<>());
            userTopicQuestionIndexMap.get(userId).put(selectedTopic, 0);

            sendNextQuestion(event, userId, selectedTopic);
        } else {
            event.reply("Topic not found. Please try again.").queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String userId = event.getAuthor().getId();
        String userMessage = event.getMessage().getContentRaw().trim();

        if (currentAnswerMap.containsKey(userId)) {
            String correctAnswer = currentAnswerMap.get(userId);

            if (userMessage.equalsIgnoreCase(correctAnswer)) {
                scoreTracker.incrementScore(userId);
                event.getChannel().sendMessage("Correct! Your score: " + scoreTracker.getScore(userId)).queue();
                currentAnswerMap.remove(userId);

                String topic = findUserTopic(userId);
                int nextQuestionIndex = userTopicQuestionIndexMap.get(userId).get(topic) + 1;
                userTopicQuestionIndexMap.get(userId).put(topic, nextQuestionIndex);

                if (nextQuestionIndex < topicQuestionsMap.get(topic).size()) {
                    sendNextQuestion(event, userId, topic);
                } else {
                    event.getChannel().sendMessage("Round over! Your final score is: " + scoreTracker.getScore(userId)).queue();
                    userTopicQuestionIndexMap.remove(userId);
                }
            } else {
                event.getChannel().sendMessage("Incorrect. Try again!").queue();
            }
        }
    }

    private void loadAllTopics() {
        // Load questions from XML files for each topic
        topicQuestionsMap.put("geography", loadQuestionsFromXML("C:\\Users\\Bipin Rai\\Desktop\\quiz\\Geography\\Geography.xml"));
        topicQuestionsMap.put("sport", loadQuestionsFromXML("C:\\Users\\Bipin Rai\\Desktop\\quiz\\Sports\\Sports.xml"));
        topicQuestionsMap.put("games", loadQuestionsFromXML("C:\\Users\\Bipin Rai\\Desktop\\quiz\\Games\\Games.xml"));
        // Add more topics as needed
    }

    private List<Question> loadQuestionsFromXML(String filePath) {
        try {
            File file = new File(filePath);
            return questionManager.parseXML(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendNextQuestion(SlashCommandInteractionEvent event, String userId, String topic) {
        int questionIndex = userTopicQuestionIndexMap.get(userId).get(topic);
        Question question = topicQuestionsMap.get(topic).get(questionIndex);
        String answer = question.getAnswers().split(",")[0].trim();

        currentAnswerMap.put(userId, answer);

        // Create and send embed
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Question " + (questionIndex + 1));
        embedBuilder.setDescription(question.getText());
        embedBuilder.setColor(Color.BLUE);

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            embedBuilder.setImage(question.getImage());
        }

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void sendNextQuestion(MessageReceivedEvent event, String userId, String topic) {
        int questionIndex = userTopicQuestionIndexMap.get(userId).get(topic);
        Question question = topicQuestionsMap.get(topic).get(questionIndex);
        String answer = question.getAnswers().split(",")[0].trim();

        currentAnswerMap.put(userId, answer);

        // Create and send embed
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Question " + (questionIndex + 1));
        embedBuilder.setDescription(question.getText());
        embedBuilder.setColor(Color.BLUE);

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            embedBuilder.setImage(question.getImage());
        }

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private String findUserTopic(String userId) {
        for (String topic : userTopicQuestionIndexMap.get(userId).keySet()) {
            if (userTopicQuestionIndexMap.get(userId).get(topic) < topicQuestionsMap.get(topic).size()) {
                return topic;
            }
        }
        return null;
    }
}
