package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot extends ListenerAdapter {
    
    /**
     * list of quotes loaded from the quotes file
     */
    private static final ArrayList<String> quotes = new ArrayList<>();
    
    /**
     * list of phrases loaded from the phrases file
     */
    private static final ArrayList<String> phrases = new ArrayList<>();
    
    /**
     * list of memes loaded from the memes folder
     */
    private static final ArrayList<File> memes = new ArrayList<>();
    
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    public static void main(String[] args) throws LoginException {
        
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
        
        try {
            Scanner fileScanner = new Scanner(new File("src/main/resources/phrases.txt"));
            while (fileScanner.hasNextLine()) phrases.add(fileScanner.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read phrases from file
        
        try {
            Scanner fileScanner = new Scanner(new File("src/main/resources/quotes.txt"));
            while (fileScanner.hasNextLine()) quotes.add(fileScanner.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read quotes from file
        
        File dir = new File("src/main/resources/memes");
        memes.addAll(Arrays.asList(dir.listFiles()));
        // load all meme files
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || e.getAuthor().isFake()) return;
        // ignore messages from bots to prevent loops
        
        String msg = e.getMessage().getContentRaw().toLowerCase();
        // convert whole message to lowercase for parsing
        
        if (e.getAuthor().getAsTag().equals("Jeremaster101#0494") && msg.equals("!reload")) {
            try {
                Scanner fileScanner = new Scanner(new File("src/main/resources/phrases.txt"));
                phrases.clear();
                while (fileScanner.hasNextLine()) phrases.add(fileScanner.nextLine());
                e.getChannel().sendMessage("Loaded " + phrases.size() + " phrases").queue();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            // read phrases from file
            
            try {
                Scanner fileScanner = new Scanner(new File("src/main/resources/quotes.txt"));
                quotes.clear();
                while (fileScanner.hasNextLine()) quotes.add(fileScanner.nextLine());
                e.getChannel().sendMessage("Loaded " + quotes.size() + " quotes").queue();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            // read quotes from file
            
            File dir = new File("src/main/resources/memes");
            memes.clear();
            memes.addAll(Arrays.asList(dir.listFiles()));
            e.getChannel().sendMessage("Loaded " + memes.size() + " memes").queue();
            // load all meme files
        }
        // reload all files
        
        for (String phrase : phrases) {
            String[] split = phrase.split(" / ");
            if (msg.contains(split[0])) {
                e.getChannel().sendMessage(split[1]).queue();
            }
        }
        // respond to a phrase if a trigger word is said
        
        if (msg.equals("!quote")) {
            Random r = new Random();
            int randInt = r.nextInt(quotes.size());
            e.getChannel().sendMessage(quotes.get(randInt)).queue();
        }
        // send random quote when "!quote" is typed
        
        if (msg.equals("!meme")) {
            Random r = new Random();
            int randInt = r.nextInt(memes.size());
            e.getChannel().sendFile(memes.get(randInt)).queue();
        }
        // send random quote when "!quote" is typed
        
    }
}