package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
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
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
     * cooldowns used for meme generation
     */
    private static final HashMap<User, Long> memeCooldowns = new HashMap<>();
    
    /**
     * number of memes created. helpful to separate meme files to prevent overwriting a meme being processed
     */
    private static int memeCount;
    
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
        MemeGenerator.loadData();
        memeCount = 0;
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
        System.out.println("Loaded " + responses.size() + " responses");
        // read phrases from file
        
        try {
            Scanner fileScanner = new Scanner(new File("text.txt"));
            texts.clear();
            while (fileScanner.hasNextLine()) texts.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded " + texts.size() + " text lines");
        // read quotes from file
        
        File dir = new File("images");
        images.clear();
        images.addAll(Arrays.asList(dir.listFiles()));
        System.out.println("Loaded " + images.size() + " images");
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
            e.getChannel().sendMessage("Loaded " + responses.size() + " responses, " + texts.size() +
                    " text lines, and " + images.size() + " images.").queue();
        }
        // reload all files
        
        for (String phrase : responses) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for (String trigger : triggers) {
                if (msg.contains(trigger.toLowerCase())) count++;
            }
            if (count == triggers.length && !e.getAuthor().isBot() && !msg.contains("!meme")) {
                e.getChannel().sendMessage(triggerAndPhrase[1]
                        .replace("$USER$", e.getAuthor().getAsMention())).queue();
            }
        }
        // respond to a phrase if a trigger word is said
    
        if(msg.startsWith("!meme")) {
            try {
                new MemeGenerator(e.getMessage()).send();
            } catch (IOException | TimeoutException | ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
                //todo logging
            }
        }
        // send random image combined with a random text when "!meme" is typed
        
//        if (msg.startsWith("!meme")) {
//            int cooldown = 60;
//            // cooldown for this command
//
//            if (!memeCooldowns.containsKey(e.getAuthor()) ||
//                    System.currentTimeMillis() - memeCooldowns.get(e.getAuthor()) >= cooldown * 1000) {
//                try {
//                    e.getChannel().sendTyping().queue();
//                    // typing indicator as loading icon
//
//                    BufferedImage image;
//                    String format;
//                    File original;
//                    String text;
//                    // variables
//
//                    if (e.getMessage().getAttachments().size() > 0 && e.getMessage().getAttachments().get(0).isImage()) {
//                        format = e.getMessage().getAttachments().get(0).getFileExtension();
//                        original = e.getMessage().getAttachments().get(0).downloadToFile(
//                                "temp/upload" + memeCount + "." + format)
//                                .get(2, TimeUnit.SECONDS);
//                        original.deleteOnExit();
//                    } else {
//                        Random r = new Random(System.nanoTime());
//                        int imageIndex = r.nextInt(images.size());
//                        original = images.get(imageIndex);
//                        format = original.getName().substring(original.getName().lastIndexOf(".") + 1);
//                    }
//                    image = ImageIO.read(original);
//                    // get random image or image from message
//
//                    String textInput = e.getMessage().getContentStripped().replace("!meme", "").trim();
//                    if (!textInput.isEmpty() || !textInput.equals("")) {
//                        text = textInput;
//                    } else {
//                        Random r = new Random((int) Math.sqrt(System.nanoTime()));
//                        text = texts.get(r.nextInt(texts.size()));
//                    }
//                    // get random or user input text
//
//                    Graphics graphics = image.getGraphics();
//                    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, image.getWidth(null) / 15));
//                    HashMap<RenderingHints.Key, Object> hints = new HashMap<>();
//                    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                    ((Graphics2D) graphics).addRenderingHints(hints);
//                    // graphics settings
//
//                    String wrapped = WordUtils.wrap(text, 20, " // ", false);
//                    String[] lines = wrapped.split(" // ");
//                    for (int i = 0; i < lines.length; i++) {
//                        String line = lines[i].trim();
//                        int outlineWidth = 2;
//                        for (int j = -outlineWidth; j <= outlineWidth; j++) {
//                            for (int k = -outlineWidth; k <= outlineWidth; k++) {
//                                graphics.setColor(Color.BLACK);
//                                graphics.drawString(line,
//                                        j + (image.getWidth(null) - ((line.length()) * (int)
//                                                (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
//                                        k + image.getHeight(null) - (int) ((lines.length - i) *
//                                                graphics.getFont().getSize() * 1.25));
//                            }
//                        }
//                        graphics.setColor(Color.WHITE);
//                        graphics.drawString(line,
//                                (image.getWidth(null) - ((line.length()) * (int)
//                                        (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
//                                image.getHeight(null) - (int) ((lines.length - i) *
//                                        graphics.getFont().getSize() * 1.25));
//                    }
//                    graphics.dispose();
//                    // apply outlined text to image
//
//                    File modified = new File("temp/meme" + memeCount + "." + format.toLowerCase());
//                    ImageIO.write(image, format.toLowerCase(), modified);
//                    e.getChannel().sendFile(modified).queueAfter(2, TimeUnit.SECONDS);
//                    modified.deleteOnExit();
//                    memeCount++;
//                    // send file and delete after sending
//
//                    memeCooldowns.put(e.getAuthor(), System.currentTimeMillis());
//
//                } catch (IOException | IllegalArgumentException | ExecutionException | InterruptedException | TimeoutException ex) {
//                    e.getChannel().sendMessage("Error generating meme! " +
//                            e.getJDA().getUserByTag("Jeremaster101#0494").getAsMention()).queue();
//                    File log = new File("log.txt");
//                    try {
//                        PrintWriter pw = new PrintWriter(log);
//                        pw.println(ex.getMessage());
//                        for (StackTraceElement ste : ex.getStackTrace()) {
//                            pw.println(ste.toString());
//                        }
//                        pw.println();
//                        pw.close();
//                    } catch (FileNotFoundException fileNotFoundException) {
//                        fileNotFoundException.printStackTrace();
//                    }
//                    ex.printStackTrace();
//                    // show error message if meme generation fails and create log
//                }
//            } else {
//                long timeLeft = cooldown - (System.currentTimeMillis() - memeCooldowns.get(e.getAuthor())) / 1000;
//                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " can generate another meme in "
//                        + timeLeft + " seconds.").queue();
//                // let user know they can't make a meme until the delay is up
//            }
//        }
        // send random image combined with a random text when "!meme" is typed
    }
}