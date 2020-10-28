package jndev.bonebot;


import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

/**
 * responder to words and phrases in a message
 *
 * @author JNDev (Jeremaster101)
 */
public class Reactor {
    
    /**
     * list of phrases loaded from the responses file
     */
    public static final ArrayList<String> reactions = new ArrayList<>();
    
    /**
     * react to a message if a trigger phrase is said
     *
     * @param message message to check and react to
     */
    public static void react(Message message) {
        String msg = message.getContentRaw().toLowerCase();
        for (String phrase : reactions) {
            String[] triggerAndEmote = phrase.split(" // ");
            String[] triggers = triggerAndEmote[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger.toLowerCase())) count++;
            }
            if (count == triggers.length) {
                message.addReaction(triggerAndEmote[1]).queue();
            }
        }
    }
    
}
