package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Random;

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
     * @throws LoginException
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
        
        if ((msg.contains("link") || msg.contains("app")) && msg.contains("?")) {
            if (msg.contains("box"))
                e.getChannel().sendMessage("> " + e.getMessage().getContentDisplay() + "\n" +
                        e.getAuthor().getAsMention() + " - Here's the link to Box: https://iastate.box.com/v/ISUCFVMB2020").queue();
            // send the box link when someone asks for the box app/link with ?
            
            if (msg.contains("band"))
                e.getChannel().sendMessage("> " + e.getMessage().getContentDisplay() + "\n" +
                        e.getAuthor().getAsMention() + " - Here's the link to Band: https://band.us/band/80638831").queue();
            // send the band link when someone asks for the band app/link with ?
        }
        
        if (msg.contains("pregame") && msg.contains("order")) {
            e.getChannel().sendMessage("> " + e.getMessage().getContentDisplay() + "\n" +
                    e.getAuthor().getAsMention() + " - Here is the order for pregame:\n" +
                    "Fights Fanfare\n" +
                    "Iowa State Fights\n" +
                    "For I For S\n" +
                    "National Anthem\n" +
                    "The Bells of Iowa State\n" +
                    "Go Cyclones Go\n" +
                    "Rise Sons of Iowa State").queue();
        }
        // send the order for pregame when someone uses "pregame" and "order" in the same message
        
        if (msg.contains("is gone") || msg.contains("am gone") || msg.contains("are gone")) {
            e.getChannel().sendMessage(":crab:").queue();
        }
        // send the crab emote whenever someone says something is/am/are gone
        
        if (msg.contains("buh")) {
            e.getChannel().sendMessage(":buh:").queue();
        }
        // send the buh emote when someone says "buh"
        
        if (msg.contains("pog")) {
            e.getChannel().sendMessage(":poggers:").queue();
        }
        // send the poggers emote when someone says "pog..."
        
        if (msg.contains("fire up") && msg.contains("emails")) {
            e.getChannel().sendMessage(":email:").queue();
        }
        // send the email emote when someone says "fire up ... emails"
        
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add("Okay you know what, fuck messenger. I turned off game notifications. It stops sending me " +
                "notifications for people starting games, but then I still get \"Sam just played BLAZAMBO for 69 " +
                "points!\" every 10 seconds. it's fucking maddening and there's no escape from it.");
        
        if(msg.equals("!quote")) {
            Random r = new Random();
            int randInt = r.nextInt(quotes.size());
            e.getChannel().sendMessage(quotes.get(randInt)).queue();
        }
        // send random quote when "!quote" is typed
        
    }
}