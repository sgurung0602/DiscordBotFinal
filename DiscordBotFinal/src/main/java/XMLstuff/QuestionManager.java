    package XMLstuff;

    import net.dv8tion.jda.api.EmbedBuilder;
    import net.dv8tion.jda.api.entities.Message;
    import net.dv8tion.jda.api.entities.Message.Attachment;
    import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
    import net.dv8tion.jda.api.entities.Member;
    import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
    import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
    import net.dv8tion.jda.api.hooks.ListenerAdapter;
    import net.dv8tion.jda.api.interactions.commands.OptionMapping;
    import net.dv8tion.jda.api.interactions.components.buttons.Button;
    import org.w3c.dom.Document;
    import org.w3c.dom.Element;
    import org.w3c.dom.Node;
    import org.w3c.dom.NodeList;

    import javax.xml.parsers.DocumentBuilder;
    import javax.xml.parsers.DocumentBuilderFactory;
    import java.awt.Color;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;
    import java.util.logging.Logger;

    public class QuestionManager extends ListenerAdapter {
        private List<Question> questions;
        private int currentQuestionIndex;
        private Question currentQuestion;
        private ScheduledExecutorService scheduler;
        private static final Logger LOGGER = Logger.getLogger(QuestionManager.class.getName());
        private boolean quizCompleted;
        private Set<String> quizmasters;
        private Message quizMessage;

        public QuestionManager() {
            scheduler = Executors.newScheduledThreadPool(1);
            quizmasters = new HashSet<>();
        }

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            switch (event.getName()) {
                case "customxml":
                    OptionMapping attachmentOption = event.getOption("file");
                    if (attachmentOption != null) {
                        Attachment attachment = attachmentOption.getAsAttachment();
                        handleQuiz(event, attachment);
                    } else {
                        event.reply("Please provide an XML file with the correct tags.").queue();
                    }
                    break;
                default:
                    event.reply("Command error not found.").queue();
                    break;
            }
        }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            String buttonId = event.getComponentId();
            String userId = event.getUser().getId();

            if (!isQuizmaster(userId)) {
                event.reply("Only the quizmaster can use this button.").setEphemeral(true).queue();
                return;
            }

            if (buttonId.equals("next_question")) {
                if (quizMessage != null) {
                    quizMessage.delete().queue();
                }
                currentQuestionIndex++;
                askNextQuestion(event.getChannel());
            } else if (buttonId.equals("reveal_answer")) {
                revealAnswer(event);
            }
        }

        private void handleQuiz(SlashCommandInteractionEvent event, Attachment attachment) {
            try {
                File xmlFile = new File("temp.xml");
                attachment.downloadToFile(xmlFile).join();

                questions = parseXML(xmlFile);

                if (!questions.isEmpty()) {
                    event.reply("Quiz is starting").queue();
                    quizCompleted = false;
                    currentQuestionIndex = 0;
                    askNextQuestion(event.getChannel());
                    addQuizmaster(event);
                } else {
                    event.reply("No questions in XML file.").queue();
                }
            } catch (Exception e) {
                LOGGER.severe("Error handling quiz: " + e.getMessage());
                event.reply("Tags are wrong").queue();
            }
        }

        private void addQuizmaster(SlashCommandInteractionEvent event) {
            Member member = event.getMember();
            if (member == null) {
                LOGGER.severe("Member is null.");
                return;
            }

            String userId = member.getId();
            quizmasters.add(userId); // Add user to the quizmasters set
            LOGGER.info("Quizmaster added: " + member.getEffectiveName());


            scheduler.schedule(() -> {
                quizmasters.remove(userId);
                LOGGER.info("Quizmaster removed: " + member.getEffectiveName());
            }, 1, TimeUnit.HOURS);
        }

        public boolean isQuizmaster(String userId) {
            return quizmasters.contains(userId);
        }

        public List<Question> parseXML(File file) throws Exception {
            List<Question> questions = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("question");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String questionText = element.getElementsByTagName("text").item(0).getTextContent();
                    String image = element.getElementsByTagName("image").item(0).getTextContent();
                    String answers = element.getElementsByTagName("answers").item(0).getTextContent();

                    Question question = new Question(questionText, image, answers);
                    questions.add(question);
                }
            }

            return questions;
        }

        private void askNextQuestion(MessageChannel channel) {
            if (quizCompleted) {
                return;
            }

            if (currentQuestionIndex >= questions.size()) {
                if (quizMessage != null) {
                    quizMessage.delete().queue();
                }
                channel.sendMessage("The quiz has finished!").queue();
                quizCompleted = true;
                return;
            }

            currentQuestion = questions.get(currentQuestionIndex);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Question " + (currentQuestionIndex + 1));
            embedBuilder.setDescription(currentQuestion.getText());
            embedBuilder.setColor(Color.BLUE);

            if (currentQuestion.getImage() != null && !currentQuestion.getImage().isEmpty()) {
                embedBuilder.setImage(currentQuestion.getImage());
            }

            channel.sendMessageEmbeds(embedBuilder.build())
                    .setActionRow(Button.primary("next_question", "Next"), Button.secondary("reveal_answer", "Reveal Answer"))
                    .queue(message -> {
                        quizMessage = message;
                    });
        }

        private void revealAnswer(ButtonInteractionEvent event) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Answers");
            embedBuilder.setDescription(currentQuestion.getAnswers());
            embedBuilder.setColor(Color.GREEN);

            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }

        public static class Question {
            private final String text;
            private final String image;
            private final String answers;

            public Question(String text, String image, String answers) {
                this.text = text;
                this.image = image;
                this.answers = answers;
            }

            public String getText() {
                return text;
            }

            public String getImage() {
                return image;
            }

            public String getAnswers() {
                return answers;
            }
        }
    }
