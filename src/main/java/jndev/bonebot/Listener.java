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
        String msg = e.getMessage().getContentRaw();
        
        Responder.respond(e.getMessage());
        
        if (msg.equals("!reload")) {
            Meme.loadData();
            Responder.loadData();
        }
        
        if (msg.startsWith("!meme")) Meme.generate(e.getMessage());
    }
}
