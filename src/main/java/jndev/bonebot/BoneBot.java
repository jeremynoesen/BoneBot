package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
        
        System.setProperty("apple.awt.UIElement", "true");
        java.awt.Toolkit.getDefaultToolkit();
        // hide dock icon on my mac
        
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
        // load data files
    }
    
    /**
     * load all data from files
     */
    private static void loadFiles() {
        try {
            Scanner fileScanner = new Scanner(new File("responses.txt"));
            responses.clear();
            while (fileScanner.hasNextLine()) responses.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read phrases from file
        
        try {
            Scanner fileScanner = new Scanner(new File("text.txt"));
            texts.clear();
            while (fileScanner.hasNextLine()) texts.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read quotes from file
        
        File dir = new File("images");
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
        String msg = e.getMessage().getContentRaw().toLowerCase();
        // convert whole message to lowercase for parsing
        
        if (msg.equals("!reload")) {
            loadFiles();
        }
        // reload all files
        
        for (String phrase : responses) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger)) count++;
            }
            if (count == triggers.length && !e.getAuthor().isBot()) {
                e.getChannel().sendMessage(triggerAndPhrase[1]
                        .replace("$USER$", e.getAuthor().getAsMention())).queue();
            }
        }
        // respond to a phrase if a trigger word is said
        
        if (msg.equals("!meme")) {
            try {
                e.getChannel().sendTyping().queue();
                Random r = new Random(System.nanoTime());
                int imageIndex = r.nextInt(images.size());
                BufferedImage image = ImageIO.read(images.get(imageIndex));
                String format = images.get(imageIndex).getName().substring(
                        images.get(imageIndex).getName().lastIndexOf(".") + 1);
                r = new Random((int) Math.sqrt(System.nanoTime()));
                String text = texts.get(r.nextInt(texts.size()));
                // load up text and image
                
                Graphics graphics = image.getGraphics();
                graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, image.getWidth(null) / 15));
                HashMap<RenderingHints.Key, Object> hints = new HashMap<>();
                hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) graphics).addRenderingHints(hints);
                // graphics settings
                
                String wrapped = WordUtils.wrap(text, 20, " // ", false);
                String[] lines = wrapped.split(" // ");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    graphics.drawString(line,
                            (image.getWidth(null) - ((line.length()) * (int)
                                    (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
                            image.getHeight(null) - (int) ((lines.length - i) *
                                    graphics.getFont().getSize() * 1.25));
                }
                graphics.dispose();
                // apply text to image
                
                File file = new File("meme." + format.toLowerCase());
                ImageIO.write(image, format.toLowerCase(), file);
                e.getChannel().sendFile(file).queue();
                file.delete();
                // send file and delete after sending
                
            } catch (IOException | IllegalArgumentException ex) {
                e.getChannel().sendMessage("Error generating meme!").queue();
                ex.printStackTrace();
                // show error message if meme generation fails
            }
        }
        // send random image combined with a random text when "!meme" is typed
    }
}