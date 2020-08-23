package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class BoneBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        
        JDABuilder.createDefault(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || e.getAuthor().isFake()) return;
        String msg = e.getMessage().getContentRaw().toLowerCase();
        
        if ((msg.contains("link") || msg.contains("app")) && msg.contains("?")) {
            if (msg.contains("box"))
                e.getChannel().sendMessage("> " + e.getMessage().getContentDisplay() + "\n" +
                        e.getAuthor().getAsMention() + " - Here's the link to Box: https://iastate.box.com/v/ISUCFVMB2020").queue();
            if (msg.contains("band"))
                e.getChannel().sendMessage("> " + e.getMessage().getContentDisplay() + "\n" +
                        e.getAuthor().getAsMention() + " - Here's the link to Band: https://band.us/band/80638831").queue();
        }
        
        if (msg.contains("is gone") || msg.contains("am gone") || msg.contains("are gone")) {
            e.getChannel().sendMessage(":crab:").queue();
        }
        
        if (msg.contains("buh")) {
            e.getChannel().sendMessage(":buh:").queue();
        }
    
        if (msg.contains("pog")) {
            e.getChannel().sendMessage(":poggers:").queue();
        }
        
    }
}