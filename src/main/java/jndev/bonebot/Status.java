package jndev.bonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * class to handle now playing status of the bot
 *
 * @author JNDev (Jeremaster101)
 */
public class Status {
    
    /**
     * list of now playing statuses loaded from the statuses file
     */
    private static final ArrayList<String> statuses = new ArrayList<>();
    
    /**
     * load all data from responses file
     */
    public static void loadData() {
        try {
            Scanner fileScanner = new Scanner(new File("statuses.txt"));
            statuses.clear();
            while (fileScanner.hasNextLine()) statuses.add(fileScanner.nextLine());
            fileScanner.close();
            statuses.trimToSize();
        } catch (FileNotFoundException e) {
            Logger.log(e);
        }
    }
    
    /**
     * set the status for the bot. this only needs to be called once
     *
     * @param jda jda to set status for
     */
    public static void setStatus(JDA jda) {
        Month month = LocalDateTime.now().getMonth();
        int day = LocalDateTime.now().getDayOfMonth();
        
        if (month == Month.OCTOBER && day == 3) {
            jda.getPresence().setActivity(Activity.playing("Oklahoma University"));
        } else if (month == Month.OCTOBER && day == 10) {
            jda.getPresence().setActivity(Activity.playing("Texas Tech"));
        } else if (month == Month.NOVEMBER && day == 7) {
            jda.getPresence().setActivity(Activity.playing("Baylor"));
        } else if (month == Month.NOVEMBER && day == 21) {
            jda.getPresence().setActivity(Activity.playing("Kansas State"));
        } else {
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException ignored) {
                    }
                    
                    Random random = new Random();
                    int hour = LocalDateTime.now().getHour();
                    DayOfWeek weekDay = LocalDateTime.now().getDayOfWeek();
                    
                    if (weekDay != DayOfWeek.SATURDAY && weekDay != DayOfWeek.SUNDAY && hour >= 17 && hour <= 18) {
                        jda.getPresence().setActivity(Activity.playing("Band Rehearsal"));
                    } else {
                        jda.getPresence().setActivity(Activity.playing(statuses.get(random.nextInt(statuses.size()))));
                    }
                }
            }).start();
        }
    }
}