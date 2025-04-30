//package XMLstuff;
//
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.components.buttons.Button;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.awt.Color;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class QuestionLoader extends ListenerAdapter {
//    private final List<Question> questions = new ArrayList<>();
//    private int currentQuestionIndex = 0;
//
//    public QuestionLoader(String filePath) {
//        loadQuestionsFromXML(filePath);
//    }
//
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        if (!event.getName().equals("xmlquestiontest")) return;
//
//        if (questions.isEmpty()) {
//            event.reply("No questions found in the XML file.").queue();
//            return;
//        }
//
//        currentQuestionIndex = 0;
//
//        askQuestion(event);
//    }
//
//    @Override
//    public void onButtonInteraction(ButtonInteractionEvent event) {
//        String buttonId = event.getComponentId();
//        if (!buttonId.startsWith("answer_")) return;
//
//        String selectedAnswer = buttonId.substring("answer_".length());
//        event.reply("You selected: " + selectedAnswer).setEphemeral(true).queue();
//
//        // You can implement logic to check if the answer is correct here
//        // For example: check if the selectedAnswer is in the list of correct answers
//        Question currentQuestion = questions.get(currentQuestionIndex - 1);
//        if (currentQuestion.getAnswers().contains(selectedAnswer)) {
//            event.getChannel().sendMessage("Correct!").queue();
//        } else {
//            event.getChannel().sendMessage("Incorrect.").queue();
//        }
//
//        askNextQuestion(event);
//    }
//
//    private void askQuestion(SlashCommandInteractionEvent event) {
//        if (currentQuestionIndex >= questions.size()) {
//            event.getChannel().sendMessage("All questions have been asked").queue();
//            return;
//        }
//
//        Question question = questions.get(currentQuestionIndex);
//        EmbedBuilder embedBuilder = new EmbedBuilder();
//        embedBuilder.setTitle("Question " + (currentQuestionIndex + 1));
//        embedBuilder.setDescription(question.getText());
//        embedBuilder.setColor(Color.BLUE);
//
//        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
//            embedBuilder.setImage(question.getImageUrl());
//        }
//
//        List<String> shuffledAnswers = new ArrayList<>(question.getAnswers());
//        Collections.shuffle(shuffledAnswers);
//
//        List<Button> buttons = new ArrayList<>();
//        for (String answer : shuffledAnswers) {
//            buttons.add(Button.primary("answer_" + answer, answer));
//        }
//
//        event.replyEmbeds(embedBuilder.build()).addActionRow(buttons).queue();
//
//        currentQuestionIndex++;
//    }
//
//    private void askNextQuestion(ButtonInteractionEvent event) {
//        if (currentQuestionIndex >= questions.size()) {
//            event.getChannel().sendMessage("Round over").queueAfter(1, TimeUnit.SECONDS);
//            return;
//        }
//
//        Question question = questions.get(currentQuestionIndex);
//        EmbedBuilder embedBuilder = new EmbedBuilder();
//        embedBuilder.setTitle("Question " + (currentQuestionIndex + 1));
//        embedBuilder.setDescription(question.getText());
//        embedBuilder.setColor(Color.BLUE);
//
//        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
//            embedBuilder.setImage(question.getImageUrl());
//        }
//
//        List<String> shuffledAnswers = new ArrayList<>(question.getAnswers());
//        Collections.shuffle(shuffledAnswers);
//
//        List<Button> buttons = new ArrayList<>();
//        for (String answer : shuffledAnswers) {
//            buttons.add(Button.primary("answer_" + answer, answer));
//        }
//
//        event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(buttons).queue();
//
//        currentQuestionIndex++;
//    }
//
//    private void loadQuestionsFromXML(String filePath) {
//        try {
//            File file = new File(filePath);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(file);
//            doc.getDocumentElement().normalize();
//
//            NodeList nList = doc.getElementsByTagName("question");
//            for (int i = 0; i < nList.getLength(); i++) {
//                Element element = (Element) nList.item(i);
//                String text = element.getElementsByTagName("text").item(0).getTextContent();
//                String imageUrl = element.getElementsByTagName("image").item(0).getTextContent();
//
//                List<String> answers = new ArrayList<>();
//                int answerIndex = 1;
//                while (true) {
//                    String answerTag = "answer" + answerIndex;
//                    if (element.getElementsByTagName(answerTag).getLength() > 0) {
//                        answers.add(element.getElementsByTagName(answerTag).item(0).getTextContent().trim());
//                        answerIndex++;
//                    } else {
//                        break;
//                    }
//                }
//
//                questions.add(new Question(text, imageUrl, answers));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    public List<Question> getQuestions() {
//        return questions;
//    }
//
//    public static class Question {
//        private final String text;
//        private final String imageUrl;
//        private final List<String> answers;
//
//        public Question(String text, String imageUrl, List<String> answers) {
//            this.text = text;
//            this.imageUrl = imageUrl;
//            this.answers = answers;
//        }
//
//        public String getText() {
//            return text;
//        }
//
//        public String getImageUrl() {
//            return imageUrl;
//        }
//
//        public List<String> getAnswers() {
//            return answers;
//        }
//
//        @Override
//        public String toString() {
//            return "Question{" +
//                    "text='" + text + '\'' +
//                    ", imageUrl='" + imageUrl + '\'' +
//                    ", answers=" + answers +
//                    '}';
//        }
//    }
//}
