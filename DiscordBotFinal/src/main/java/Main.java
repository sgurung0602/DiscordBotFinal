
//import XMLstuff.QuestionLoader;
import XMLstuff.QuestionManager;
import commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {
        ScoreTracker scoreTracker = new ScoreTracker();


        QuestionManager questionManager = new QuestionManager();

        JDA jda = JDABuilder.createLight("MTI0NzIyNDYwMjk1NDgyNTc2Mg.G7Ce-w._4ar1IAK2p2NHY-eUI8TwOvRK_CpSbl8jb_rSE", GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(
                        new Listener(scoreTracker, questionManager),
                        new triviaQuestion(scoreTracker),
                        new Leaderboard(scoreTracker, questionManager),
//                        new imageQuestion(scoreTracker),
                        questionManager
//                        new QuestionLoader("C:\\Users\\Bipin Rai\\Desktop\\DiscordBotFinal\\src\\main\\java\\XMLstuff\\questions.xml")
                )
                .build();
    }
}

