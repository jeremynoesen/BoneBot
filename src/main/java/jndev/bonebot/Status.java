package jndev.bonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.Random;

/**
 * class to handle now playing status of the bot
 *
 * @author JNDev (Jeremaster101)
 */
public class Status {
    
    /**
     * list of now playing statuses loaded from the statuses file
     */
    public static final ArrayList<String> statuses = new ArrayList<>();
    
    /**
     * set the status for the bot. this only needs to be called once
     *
     * @param jda jda to set status for
     */
    public static void setStatus(JDA jda) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ignored) {
                }
                Random random = new Random();
                jda.getPresence().setActivity(Activity.playing(statuses.get(random.nextInt(statuses.size()))));
            }
        }).start();
    }
}