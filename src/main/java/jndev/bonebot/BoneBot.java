package jndev.bonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Random;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot {
    
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    public static void main(String[] args) throws LoginException {
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        
        JDA jda = JDABuilder.createLight(args[0])
                .addEventListeners(new Listener())
                .setActivity(Activity.playing("Trombone"))
                .build();
        
        Responder.loadData();
        Meme.loadData();
        
        long prev = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - prev >= 30000) {
                prev = System.currentTimeMillis();
                Random random = new Random();
                int randInt = random.nextInt(11);
                switch (randInt) {
                    case 0:
                        jda.getPresence().setActivity(Activity.playing("Bone Cheer"));
                        break;
                    case 1:
                        jda.getPresence().setActivity(Activity.playing("Beer Barrel"));
                        break;
                    case 2:
                        jda.getPresence().setActivity(Activity.playing("Hero"));
                        break;
                    case 3:
                        jda.getPresence().setActivity(Activity.playing("Sleepers, Wake!"));
                        break;
                    case 4:
                        jda.getPresence().setActivity(Activity.playing("Pregame, off the field"));
                        break;
                    case 5:
                        jda.getPresence().setActivity(Activity.playing("Bass Trombone"));
                        break;
                    case 6:
                        jda.getPresence().setActivity(Activity.watching("Spaceballs"));
                        break;
                    case 7:
                        jda.getPresence().setActivity(Activity.watching("Top Secret"));
                        break;
                    case 8:
                        jda.getPresence().setActivity(Activity.playing("Full Throwdown"));
                        break;
                    case 9:
                        jda.getPresence().setActivity(Activity.playing("Crab Rave"));
                        break;
                    case 10:
                        jda.getPresence().setActivity(Activity.playing("Trombone"));
                        break;
                }
            }
        }
    }
}