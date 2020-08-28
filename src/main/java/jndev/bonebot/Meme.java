package jndev.bonebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Meme {
    
    /**
     * list of texts loaded from the texts file
     */
    private static final ArrayList<String> texts = new ArrayList<>();
    
    /**
     * list of images loaded from the images folder
     */
    private static final ArrayList<BufferedImage> images = new ArrayList<>();
    
    /**
     * cooldowns used for meme generation
     */
    private static final HashMap<User, Long> memeCooldowns = new HashMap<>();
    
    /**
     * number of memes created. helpful to separate meme files to prevent overwriting a meme being processed
     */
    private static int memeCount;
    
    /**
     * cooldown for use of meme generation
     */
    private static final int cooldown = 60;
    
    /**
     * meme text
     */
    private String text;
    
    /**
     * background image
     */
    private BufferedImage image;
    
    /**
     * generated meme image
     */
    private BufferedImage meme;
    
    /**
     * command message
     */
    private final Message command;
    
    /**
     * constructs a new meme generator class with the original message for get command arguments from
     *
     * @param command command containing meme arguments
     */
    public Meme(Message command) {
        this.command = command;
    }
    
    /**
     * load external data files
     */
    public static void loadData() {
        try {
            Scanner fileScanner = new Scanner(new File("text.txt"));
            texts.clear();
            while (fileScanner.hasNextLine()) texts.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            Logger.log(e);
        }
        
        File dir = new File("images");
        images.clear();
        for (File file : dir.listFiles()) {
            try {
                images.add(ImageIO.read(file));
            } catch (IOException e) {
                Logger.log(e);
            }
        }
    }
    
    /**
     * send a meme to a channel
     */
    public void generate() {
        try {
            if (checkCooldown()) {
                command.getChannel().sendTyping().queue();
                setText();
                setImage();
                processImage();
                command.getChannel().sendFile(convertToFile()).queueAfter(2, TimeUnit.SECONDS);
                updateCooldown();
            }
        } catch (IOException | TimeoutException | ExecutionException | InterruptedException exception) {
            command.getChannel().sendMessage("Error generating meme! " +
                    command.getJDA().getUserByTag("Jeremaster101#0494").getAsMention());
            Logger.log(exception);
            
        }
    }
    
    /**
     * check if a user's cooldown is up so they can use the meme generator
     *
     * @return true if they can generate a meme
     */
    private boolean checkCooldown() {
        if (memeCooldowns.containsKey(command.getAuthor())) {
            if (System.currentTimeMillis() - memeCooldowns.get(command.getAuthor()) >= cooldown * 1000) {
                return true;
            } else {
                long timeLeft = cooldown - (System.currentTimeMillis() - memeCooldowns.get(command.getAuthor())) / 1000;
                command.getChannel().sendMessage(command.getAuthor().getAsMention() + " can generate another meme in "
                        + timeLeft + " seconds.").queue();
                return false;
            }
        } else {
            return true;
        }
    }
    
    /**
     * set the text input from a discord message or a random to use to generate a meme
     */
    private void setText() {
        String input = command.getContentStripped().replace("!meme", "").trim();
        if (!input.isEmpty() || !input.equals("")) {
            this.text = input;
        } else {
            Random r = new Random((int) Math.sqrt(System.nanoTime()));
            this.text = texts.get(Math.abs(r.nextInt(texts.size())));
        }
    }
    
    /**
     * read an image from a discord message or a random to use to generate a meme
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     */
    private void setImage() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        if (command.getAttachments().size() > 0 && command.getAttachments().get(0).isImage()) {
            File file = command.getAttachments().get(0).downloadToFile(
                    "temp/upload" + memeCount + ".png")
                    .get(2, TimeUnit.SECONDS);
            file.deleteOnExit();
            this.image = ImageIO.read(file);
        } else {
            Random r = new Random(System.nanoTime());
            this.image = images.get(Math.abs(r.nextInt(images.size())));
        }
    }
    
    /**
     * generate a meme using the input text and image
     */
    private void processImage() {
        double ratio = image.getHeight() / (double) image.getWidth();
        int width = 512;
        int height = (int) (512 * ratio);
        meme = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = meme.getGraphics();
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, meme.getWidth(null) / 15));
        HashMap<RenderingHints.Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        ((Graphics2D) graphics).addRenderingHints(hints);
        graphics.drawImage(image, 0, 0, width, height, null);
        
        String wrapped = WordUtils.wrap(text, 24, " // ", false);
        String[] lines = wrapped.split(" // ");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int outlineWidth = 2;
            for (int j = -outlineWidth; j <= outlineWidth; j++) {
                for (int k = -outlineWidth; k <= outlineWidth; k++) {
                    graphics.setColor(Color.BLACK);
                    graphics.drawString(line,
                            j + (meme.getWidth(null) - ((line.length()) * (int)
                                    (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
                            k + meme.getHeight(null) - (int) ((lines.length - i) *
                                    graphics.getFont().getSize() * 1.25));
                }
            }
            graphics.setColor(Color.WHITE);
            graphics.drawString(line,
                    (meme.getWidth(null) - ((line.length()) * (int)
                            (graphics.getFont().getSize2D() * 5.0 / 8.1))) / 2,
                    meme.getHeight(null) - (int) ((lines.length - i) *
                            graphics.getFont().getSize() * 1.25));
        }
        graphics.dispose();
    }
    
    /**
     * convert the meme image to a file, save it to the temp folder, and set it to auto delete later
     *
     * @return meme image converted to file
     * @throws IOException
     */
    private File convertToFile() throws IOException {
        File file = new File("temp/meme" + memeCount + ".png");
        ImageIO.write(meme, "png", file);
        file.deleteOnExit();
        memeCount++;
        return file;
    }
    
    /**
     * update a user's cooldown time to the current time
     */
    private void updateCooldown() {
        memeCooldowns.put(command.getAuthor(), System.currentTimeMillis());
    }
}
