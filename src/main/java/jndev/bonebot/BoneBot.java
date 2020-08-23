package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class BoneBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        String msg = e.getMessage().getContentStripped();
        if (msg.toLowerCase().contains("test")) {
            e.getChannel().sendMessage("This works!").queue();
        }
    }
}