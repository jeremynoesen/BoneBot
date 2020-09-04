package jndev.bonebot;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * responder to words and phrases in a message
 */
public class Responder {
    
    /**
     * list of phrases loaded from the responses file
     */
    private static final ArrayList<String> responses = new ArrayList<>();
    
    /**
     * load all data from responses file
     */
    public static void loadData() {
        try {
            Scanner fileScanner = new Scanner(new File("responses.txt"));
            responses.clear();
            while (fileScanner.hasNextLine()) responses.add(fileScanner.nextLine());
            fileScanner.close();
            responses.trimToSize();
        } catch (FileNotFoundException e) {
            Logger.log(e);
        }
    }
    
    /**
     * respond to a message if a trigger phrase is said
     *
     * @param message message to check and respond to
     */
    public static void respond(Message message) {
        String msg = message.getContentRaw().toLowerCase();
        for (String phrase : responses) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger.toLowerCase())) count++;
            }
            if (count == triggers.length) {
                message.getJDA().getPresence().setActivity(Activity.playing("Discord"));
                message.getChannel().sendMessage(triggerAndPhrase[1]
                        .replace("$USER$", message.getAuthor().getAsMention())).queue();
            }
        }
    }
    
}
