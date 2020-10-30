package jndev.bonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Halloween trick or treating virtual event
 *
 * @author JNDev (Jeremaster101)
 */
public class Halloween extends ListenerAdapter {
    
    /**
     * user candy counts
     */
    private final HashMap<User, Integer> data;
    
    /**
     * trick or treat cooldown
     */
    private final HashMap<User, Long> totCooldowns;
    
    /**
     * give cooldown
     */
    private final HashMap<User, Long> giveCooldowns;
    
    /**
     * give cooldown
     */
    private final HashMap<User, Long> stealCooldowns;
    
    /**
     * initialize the entire halloween event
     *
     * @param jda jda this is running in
     */
    public Halloween(JDA jda) {
        data = new HashMap<>();
        totCooldowns = new HashMap<>();
        giveCooldowns = new HashMap<>();
        stealCooldowns = new HashMap<>();
        loadData(jda);
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveData));
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            String msg = e.getMessage().getContentRaw();
            
            if (msg.startsWith("!halloween")) {
                
                showInfo(e.getChannel());
                
            } else {
                
                Month month = LocalDateTime.now().getMonth();
                int day = LocalDateTime.now().getDayOfMonth();
                if (month == Month.OCTOBER && day == 31) {
                    
                    if (msg.startsWith("!trickortreat") || msg.startsWith("!tot")) {
                        
                        if (!totCooldowns.containsKey(e.getAuthor()) ||
                                System.currentTimeMillis() - totCooldowns.get(e.getAuthor()) >= 10000) {
                            
                            totCooldowns.put(e.getAuthor(), System.currentTimeMillis());
                            Random random = new Random();
                            
                            if (random.nextInt(3) == 0) {
                                takeCandy(e.getAuthor(), e.getChannel());
                            } else {
                                giveCandy(e.getAuthor(), e.getChannel());
                            }
                            
                        } else {
                            
                            MessageEmbed messageEmbed = createEmbed("Wait!",
                                    e.getAuthor().getAsMention() + " can trick or treat in " + (10 -
                                            ((System.currentTimeMillis() - totCooldowns.get(e.getAuthor())) / 1000)) + " seconds",
                                    Color.ORANGE);
                            e.getChannel().sendMessage(messageEmbed).queue();
                        }
                        
                    } else if (msg.startsWith("!give")) {
                        
                        if (!giveCooldowns.containsKey(e.getAuthor()) ||
                                System.currentTimeMillis() - giveCooldowns.get(e.getAuthor()) >= 30000) {
                            
                            if (e.getMessage().getMentionedUsers().size() == 1) {
                                giveCandy(e.getAuthor(),
                                        e.getMessage().getMentionedUsers().get(0), e.getChannel());
                            } else {
                                MessageEmbed messageEmbed = createEmbed("Error!",
                                        "That command requires a mentioned user",
                                        Color.ORANGE);
                                e.getChannel().sendMessage(messageEmbed).queue();
                            }
                            
                        } else {
                            
                            MessageEmbed messageEmbed = createEmbed("Wait!",
                                    e.getAuthor().getAsMention() + " can give 🍫 in " + (30 -
                                            ((System.currentTimeMillis() - giveCooldowns.get(e.getAuthor())) / 1000)) + " seconds",
                                    Color.ORANGE);
                            e.getChannel().sendMessage(messageEmbed).queue();
                        }
                        
                    } else if (msg.startsWith("!steal")) {
                        
                        if (!stealCooldowns.containsKey(e.getAuthor()) ||
                                System.currentTimeMillis() - stealCooldowns.get(e.getAuthor()) >= 60000) {
                            
                            if (e.getMessage().getMentionedUsers().size() == 1) {
                                Random random = new Random();
                                
                                if (random.nextInt(3) == 0) {
                                    takeCandy(e.getMessage().getMentionedUsers().get(0), e.getAuthor(),
                                            e.getChannel());
                                } else {
                                    MessageEmbed messageEmbed = createEmbed("Failed Theft!",
                                            e.getAuthor().getAsMention() + " tried to steal 🍫 from " +
                                                    e.getMessage().getMentionedUsers().get(0).getAsMention() + " but failed",
                                            Color.ORANGE);
                                    e.getChannel().sendMessage(messageEmbed).queue();
                                }
                            } else {
                                MessageEmbed messageEmbed = createEmbed("Error!",
                                        "That command requires a mentioned user",
                                        Color.ORANGE);
                                e.getChannel().sendMessage(messageEmbed).queue();
                            }
                            
                        } else {
                            
                            MessageEmbed messageEmbed = createEmbed("Wait!",
                                    e.getAuthor().getAsMention() + " can steal 🍫 in " + (60 -
                                            ((System.currentTimeMillis() - stealCooldowns.get(e.getAuthor())) / 1000)) + " seconds",
                                    Color.ORANGE);
                            e.getChannel().sendMessage(messageEmbed).queue();
                        }
                        
                    } else if (msg.startsWith("!bag")) {
                        
                        getBag(e.getAuthor(), e.getChannel());
                        
                    } else if (msg.startsWith("!leaderboard") || msg.startsWith("!lb")) {
                        
                        showLeaderboard(e.getChannel());
                        
                    }
                    
                } else if (msg.startsWith("!trickortreat") || msg.startsWith("!tot") || msg.startsWith("!bag") ||
                        msg.startsWith("!leaderboard") || msg.startsWith("!lb") || msg.startsWith("!give") ||
                        msg.startsWith("!steal")) {
                    
                    MessageEmbed messageEmbed = createEmbed("It Is Not Halloween!",
                            "This can only be done on Halloween",
                            Color.ORANGE);
                    e.getChannel().sendMessage(messageEmbed).queue();
                }
            }
        }
    }
    
    /**
     * load all data from file to the hash map
     */
    private void loadData(JDA jda) {
        try {
            Scanner fileScanner = new Scanner(new File("halloween.txt"));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                String[] parts = line.split(" ");
                User user = jda.retrieveUserById(parts[0].trim()).complete();
                int count = Integer.parseInt(parts[1].trim());
                data.put(user, count);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            new File("halloween.txt");
        }
    }
    
    /**
     * save all data from hashmap to file
     */
    private void saveData() {
        try {
            PrintWriter printWriter = new PrintWriter(new File("halloween.txt"));
            for (User user : data.keySet()) {
                printWriter.println(user.getId() + " " + data.get(user));
            }
            printWriter.close();
        } catch (IOException e) {
            Logger.log(e);
        }
    }
    
    /**
     * create an embed with a title, body, and color
     *
     * @param title   title of embed
     * @param message message in embed
     * @param color   color of embed
     * @return built embed message
     */
    private MessageEmbed createEmbed(String title, String message, Color color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(message);
        embedBuilder.setColor(color);
        return embedBuilder.build();
    }
    
    /**
     * give a user a candy and show an embed
     *
     * @param user    user to give candy to
     * @param channel channel to send embed in
     */
    private void giveCandy(User user, MessageChannel channel) {
        if (data.containsKey(user)) {
            data.put(user, data.get(user) + 1);
        } else {
            data.put(user, 1);
        }
        MessageEmbed messageEmbed = createEmbed("Treat!",
                user.getAsMention() + " received 1 🍫", Color.GREEN);
        channel.sendMessage(messageEmbed).queue();
    }
    
    /**
     * take candy from a user and show the embed
     *
     * @param user    user to take candy from
     * @param channel channel to send embed in
     */
    private void takeCandy(User user, MessageChannel channel) {
        if (data.containsKey(user) && data.get(user) >= 1) {
            data.put(user, data.get(user) - 1);
            MessageEmbed messageEmbed = createEmbed("Trick!",
                    user.getAsMention() + " lost 1 🍫", Color.ORANGE);
            channel.sendMessage(messageEmbed).queue();
        } else {
            data.put(user, 0);
            MessageEmbed messageEmbed = createEmbed("Trick!",
                    user.getAsMention() + " had no 🍫 to lose", Color.ORANGE);
            channel.sendMessage(messageEmbed).queue();
        }
    }
    
    /**
     * get the user's candy bag to show how much candy they have
     *
     * @param user    user to get bag for
     * @param channel channel to send embed to
     */
    private void getBag(User user, MessageChannel channel) {
        if (!data.containsKey(user)) {
            data.put(user, 0);
        }
        MessageEmbed messageEmbed = createEmbed("Candy Bag",
                user.getAsMention() + " currently has " + data.get(user) + " 🍫", Color.MAGENTA);
        channel.sendMessage(messageEmbed).queue();
    }
    
    /**
     * show the game how to play message
     *
     * @param channel channel to send embed to
     */
    private void showInfo(MessageChannel channel) {
        MessageEmbed messageEmbed = createEmbed(
                "Halloween Trick or Treat Virtual Competition",
                "How to play:\n" +
                        "1. Type !trickortreat or !tot (10 second cooldown)\n" +
                        "2. Receive a treat, or be tricked and lose candy\n" +
                        "3. Type !bag to see how much candy you have\n" +
                        "4. Type !leaderboard or !lb to see the top 10 users\n" +
                        "5. Give people candy with !give @user (30 second cooldown)\n" +
                        "6. Try to steal candy with !steal @user (60 second cooldown)\n" +
                        "7. Get the most candy by the end of Halloween\n" +
                        "8. Win 1 month of Discord Nitro Classic\n" +
                        "\n" +
                        "Trick or treating starts on Halloween. Good luck!",
                Color.MAGENTA);
        channel.sendMessage(messageEmbed).queue();
    }
    
    /**
     * show the leaderboard showing the top 10 users
     *
     * @param channel channel to send embed to
     */
    private void showLeaderboard(MessageChannel channel) {
        int curCount = -1;
        User curUser = null;
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        for (int i = 0; i < Math.min(data.size(), 10); i++) {
            for (User user : data.keySet()) {
                if (!users.contains(user) && data.get(user) > curCount) {
                    curUser = user;
                    curCount = data.get(user);
                }
            }
            if (curCount > 0) {
                users.add(curUser);
                counts.add(curCount);
            }
            curUser = null;
            curCount = -1;
        }
        StringBuilder leaderboard = new StringBuilder();
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                int count = counts.get(i);
                leaderboard.append(i + 1).append(". ").append(user.getName()).append(" - ").append(count).append(" 🍫 \n");
            }
        } else {
            leaderboard.append("No users yet");
        }
        MessageEmbed messageEmbed = createEmbed("Leaderboard", leaderboard.toString(), Color.MAGENTA);
        channel.sendMessage(messageEmbed).queue();
    }
    
    /**
     * give a user a piece of candy
     *
     * @param from    user sending the candy
     * @param to      user receiving the candy
     * @param channel channel to send embeds to
     */
    private void giveCandy(User from, User to, MessageChannel channel) {
        if (data.containsKey(from) && data.get(from) >= 1) {
            data.put(from, data.get(from) - 1);
            if (data.containsKey(to)) {
                data.put(to, data.get(to) + 1);
            } else {
                data.put(to, 1);
            }
            MessageEmbed messageEmbed = createEmbed("Candy Given!",
                    to.getAsMention() + " received 1 🍫 from " + from.getAsMention(), Color.GREEN);
            channel.sendMessage(messageEmbed).queue();
            giveCooldowns.put(from, System.currentTimeMillis());
        } else {
            MessageEmbed messageEmbed = createEmbed("No Candy!",
                    from.getAsMention() + " does not have any 🍫", Color.ORANGE);
            channel.sendMessage(messageEmbed).queue();
        }
    }
    
    /**
     * take a piece of candy from another user
     *
     * @param from    user being taken from
     * @param to      user taking the candy
     * @param channel channel to send embeds to
     */
    private void takeCandy(User from, User to, MessageChannel channel) {
        if (data.containsKey(from) && data.get(from) >= 1) {
            data.put(from, data.get(from) - 1);
            if (data.containsKey(to)) {
                data.put(to, data.get(to) + 1);
            } else {
                data.put(to, 1);
            }
            MessageEmbed messageEmbed = createEmbed("Candy Stolen!",
                    to.getAsMention() + " stole 1 🍫 from " + from.getAsMention(), Color.GREEN);
            channel.sendMessage(messageEmbed).queue();
            stealCooldowns.put(to, System.currentTimeMillis());
        } else {
            MessageEmbed messageEmbed = createEmbed("No Candy!",
                    from.getAsMention() + " does not have any 🍫", Color.ORANGE);
            channel.sendMessage(messageEmbed).queue();
        }
    }
}
