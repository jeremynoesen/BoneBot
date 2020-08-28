package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot extends ListenerAdapter {
    
    /**
     * list of phrases loaded from the responses file
     */
    private static final ArrayList<String> responses = new ArrayList<>();
    
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    public static void main(String[] args) throws LoginException {
        
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        // hide dock icon on my mac
        
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // prevent bot from starting without a token
        
        JDABuilder.createLight(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
        // initialize bot
        
        loadFiles();
        Meme.loadData();
        // load data files
    }
    
    /**
     * load all data from files
     */
    private static void loadFiles() {
        try {
            Scanner fileScanner = new Scanner(new File("responses.txt"));
            responses.clear();
            while (fileScanner.hasNextLine()) responses.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded " + responses.size() + " responses");
        // read phrases from file
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw().toLowerCase();
        // convert whole message to lowercase for parsing
        
        if (msg.equals("!reload")) {
            loadFiles();
            Meme.loadData();
        }
        // reload all files
        
        for (String phrase : responses) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger.toLowerCase())) count++;
            }
            if (count == triggers.length && !e.getAuthor().isBot() && !msg.contains("!meme")) {
                e.getChannel().sendMessage(triggerAndPhrase[1]
                        .replace("$USER$", e.getAuthor().getAsMention())).queue();
            }
        }
        // respond to a phrase if a trigger word is said
        
        if (msg.startsWith("!meme")) new Meme(e.getMessage()).generate();
        // send random image combined with a random text when "!meme" is typed
    }
}