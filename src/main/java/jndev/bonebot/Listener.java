package jndev.bonebot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * all listeners for the bot
 */
public class Listener extends ListenerAdapter {
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            
            Responder.respond(e.getMessage());
            
            if (e.getMessage().getContentRaw().startsWith("!meme")) {
                Meme.generate(e.getMessage());
                Runtime.getRuntime().gc();
            }
            
        }
    }
}