package jndev.bonebot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

/**
 * all listeners for the bot
 *
 * @author JNDev (Jeremaster101)
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
            if (e.getMessage().getContentRaw().startsWith("!meme")) {
                Meme.generate(e.getMessage());
                Runtime.getRuntime().gc();
            } else if (e.getMessage().getContentRaw().startsWith("!restart")) {
                e.getChannel().sendMessage("Restarting...");
                System.exit(0);
            } else {
                Responder.respond(e.getMessage());
                Reactor.react(e.getMessage());
            }
        }
    }
}