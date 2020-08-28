package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot extends ListenerAdapter {
    
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
        
        JDABuilder.createLight(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
        
        Meme.loadData();
        Responder.loadData();
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw();
    
        Responder.respond(e.getMessage());
    
        if (msg.equals("!reload")) {
            Meme.loadData();
            Responder.loadData();
        }
        
        if (msg.startsWith("!meme")) new Meme(e.getMessage()).generate();
    }
}