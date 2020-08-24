package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot extends ListenerAdapter {
    
    /**
     * list of texts loaded from the texts file
     */
    private static final ArrayList<String> texts = new ArrayList<>();
    
    /**
     * list of phrases loaded from the responses file
     */
    private static final ArrayList<String> responses = new ArrayList<>();
    
    /**
     * list of images loaded from the images folder
     */
    private static final ArrayList<File> images = new ArrayList<>();
    
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    public static void main(String[] args) throws LoginException {
        
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // prevent bot from starting without a token
        
        JDABuilder.createLight(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
        // initialize bot
        
        loadFiles();
    }
    
    /**
     * load all data from files
     */
    private static void loadFiles() {
        try {
            Scanner fileScanner = new Scanner(new File("src/main/resources/responses.txt"));
            responses.clear();
            while (fileScanner.hasNextLine()) responses.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read phrases from file
        
        try {
            Scanner fileScanner = new Scanner(new File("src/main/resources/text.txt"));
            texts.clear();
            while (fileScanner.hasNextLine()) texts.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read quotes from file
        
        File dir = new File("src/main/resources/images");
        images.clear();
        images.addAll(Arrays.asList(dir.listFiles()));
        // load all meme files
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || e.getAuthor().isFake()) return;
        // ignore messages from bots to prevent loops
        
        String msg = e.getMessage().getContentRaw().toLowerCase();
        // convert whole message to lowercase for parsing
        
        if (msg.equals("!reload")) {
            loadFiles();
            e.getMessage().delete().queue();
        }
        // reload all files
        
        for (String phrase : responses) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger)) count++;
            }
            if (count == triggers.length) {
                e.getChannel().sendMessage(triggerAndPhrase[1]).queue();
            }
        }
        // respond to a phrase if a trigger word is said

//        if (msg.equals("!meme")) {
//            Random r = new Random(System.nanoTime());
//            e.getMessage().delete().queue();
//            e.getChannel().sendFile(images.get(r.nextInt(images.size()))).queue();
//            r = new Random(System.nanoTime() - System.currentTimeMillis());
//            e.getChannel().sendMessage(texts.get(r.nextInt(texts.size()))).queue();
//        }
//        // send random image combined with a random text when "!meme" is typed
        
        if (msg.equals("!meme")) {
            try {
                e.getMessage().delete().queue();
                Random r = new Random(System.nanoTime());
                int imageIndex = r.nextInt(images.size());
                Image image = ImageIO.read(images.get(imageIndex));
                String format = images.get(imageIndex).getName().split("\\.")[1];
                r = new Random(System.nanoTime() - System.currentTimeMillis());
                String text = texts.get(r.nextInt(texts.size()));
                
                Graphics graphics = image.getGraphics();
//                graphics.setFont(graphics.getFont().deriveFont(image.getWidth(null) / 18f));
                graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, image.getWidth(null) / 15));
                String wrapped = WordUtils.wrap(text, 25, " // ", false);
                String[] lines = wrapped.split(" // ");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    graphics.drawString(line,
                            (image.getWidth(null) - ((line.length()) * (int) (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
                            image.getHeight(null) - (int) ((lines.length - i) * graphics.getFont().getSize() * 1.25));
                }
                graphics.dispose();
                File file = new File("meme." + format);
                ImageIO.write((RenderedImage) image, format.toLowerCase(), file);
                e.getChannel().sendFile(file).queue();
                
            } catch (IOException ex) {
                e.getChannel().sendMessage("Error generating meme!").queue();
                ex.printStackTrace();
            }
        }
        // send random image combined with a random text when "!meme" is typed
    }
}