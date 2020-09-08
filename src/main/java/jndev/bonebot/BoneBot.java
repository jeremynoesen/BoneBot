package jndev.bonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
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
        
        Random random = new Random();
        
        ArrayList<String> msgs = new ArrayList<>();
        msgs.add("Bone Cheer");
        msgs.add("Beer Barrel");
        msgs.add("Hero");
        msgs.add("Sleepers, Wake!");
        msgs.add("Pregame, off the field");
        msgs.add("Bass Trombone");
        msgs.add("Spaceballs");
        msgs.add("Top Secret");
        msgs.add("Full Throwdown");
        msgs.add("Crab Rave");
        msgs.add("Trombone");
        
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {
                }
                
                jda.getPresence().setActivity(Activity.playing(msgs.get(random.nextInt(msgs.size()))));
                
            }
        }).start();
    }
}