package jndev.bonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

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
                .build();
        
        Halloween halloween = new Halloween(jda);
        jda.addEventListener(halloween);
        
        ImageIO.setUseCache(false);
        Loader.loadData("responses.txt", Responder.responses);
        Loader.loadData("texts.txt", Meme.texts);
        Loader.loadData("statuses.txt", Status.statuses);
        Loader.loadData("reactions.txt", Reactor.reactions);
    
        Status.setStatus(jda);
    }
}