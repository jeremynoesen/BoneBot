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
                try {
                    e.getChannel().sendMessage("Restarting...").queue();
                    Runtime.getRuntime().exec("/bin/sh restart.command");
                } catch (IOException ioException) {
                    Logger.log(ioException);
                }
            } else if (e.getMessage().getContentRaw().startsWith("!update")) {
                try {
                    e.getChannel().sendMessage("Updating from GitHub...").queue();
                    Runtime.getRuntime().exec("/bin/sh update.command");
                    e.getChannel().sendMessage("Done!").queue();
                } catch (IOException ioException) {
                    Logger.log(ioException);
                }
            } else {
                Responder.respond(e.getMessage());
                Reactor.react(e.getMessage());
            }
        }
    }
}