package jndev.bonebot.modules

import jndev.bonebot.config.Config
import jndev.bonebot.util.Logger
import net.dv8tion.jda.api.entities.Message
import org.apache.commons.text.WordUtils
import java.awt.*
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

/**
 * meme generator
 *
 * @author JNDev (Jeremaster101)
 */
class Meme
/**
 * constructs a new meme generator class with the original message for get command arguments from
 *
 * @param command command containing meme arguments
 */
private constructor(
    /**
     * command message
     */
    private val command: Message
) {
    /**
     * meme text
     */
    private var text: String? = null

    /**
     * background image
     */
    private var image: BufferedImage? = null

    /**
     * generated meme image
     */
    private var meme: BufferedImage? = null

    /**
     * generate and send a meme
     */
    private fun generate() {
        if ((System.currentTimeMillis() - prevTime) >= Config.memeCooldown * 1000) {
            try {
                command.channel.sendTyping().queue()
                readTextAndImage()
                processImage()
                val file = convertToFile()
                command.channel.sendFile(file).queue()
                image!!.flush()
                meme!!.flush()
                image = null
                meme = null
                text = null
                file.delete()
                prevTime = System.currentTimeMillis()
                System.gc()
            } catch (exception: IOException) {
                command.channel.sendMessage(
                    "Error generating meme! " +
                            command.jda.getUserByTag("Jeremaster101#0494")!!.asMention
                ).queue()
                Logger.log(exception)
            } catch (exception: FontFormatException) {
                command.channel.sendMessage(
                    "Error generating meme! " +
                            command.jda.getUserByTag("Jeremaster101#0494")!!.asMention
                ).queue()
                Logger.log(exception)
            }
        } else {
            command.delete().queue()
        }
    }

    /**
     * read an image from a discord message, user mention, or a random to use to generate a meme, as well as grab the
     * text from the message or use a random to make a meme
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readTextAndImage() {
        var input = command.contentRaw.replaceFirst("bbmeme".toRegex(), "").trim { it <= ' ' }
        if (command.attachments.size > 0 && command.attachments[0].isImage) {
            image = ImageIO.read(URL(command.attachments[0].url))
        } else if (command.mentionedUsers.size > 0) {
            image = ImageIO.read(URL(command.mentionedUsers[0].effectiveAvatarUrl))
            for (i in command.mentionedUsers.indices) input = input.replace(command.mentionedUsers[i].asMention, "")
                .replace("<@!" + command.mentionedUsers[i].idLong + ">", "")
                .replace("  ", " ").trim { it <= ' ' }
        } else {
            val r = Random()
            val dir = File("images")
            var rand = r.nextInt(dir.listFiles()!!.size)
            while (dir.listFiles()!![rand].isHidden) {
                rand = r.nextInt(dir.listFiles()!!.size)
            }
            image = ImageIO.read(dir.listFiles()!![rand])
        }
        text = if (input.isNotEmpty() || input != "") {
            input
        } else {
            val r = Random()
            texts[r.nextInt(texts.size)]
        }
    }

    /**
     * generate a meme using the input text and image
     */
    @Throws(IOException::class, FontFormatException::class)
    private fun processImage() {
        val ratio = image!!.height / image!!.width.toDouble()
        val width = 1024
        val height = (width * ratio).toInt()
        meme = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = meme!!.graphics
        val g2d = graphics as Graphics2D
        val fontFile = File("/System/Library/Fonts/Supplemental/Impact.ttf")
        val font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(96f)
        graphics.setFont(font)
        val metrics = graphics.getFontMetrics(font)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.drawImage(image, 0, 0, width, height, null)
        val lines = ArrayList<String>()
        val sections = text!!.split("\n").toTypedArray()
        for (section in sections) lines.addAll(
            listOf(
                *WordUtils.wrap(section, 18, "\n", true).split("\n").toTypedArray()
            )
        )
        for (i in lines.indices) {
            val line = lines[i].trim { it <= ' ' }.toUpperCase()
            if (line.isEmpty()) continue
            graphics.setColor(Color.WHITE)
            graphics.drawString(
                line,
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                (meme!!.getHeight(null) - (lines.size - i - 0.75) * graphics.getFont().size).toInt()
            )
            val shape = TextLayout(line, font, g2d.fontRenderContext).getOutline(null)
            g2d.stroke = BasicStroke(3f)
            g2d.translate(
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                (meme!!.getHeight(null) - (lines.size - i - 0.75) * graphics.getFont().size).toInt()
            )
            graphics.setColor(Color.BLACK)
            g2d.draw(shape)
            g2d.translate(
                (-((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0)).toInt(),
                (-(meme!!.getHeight(null) - (lines.size - i - 0.75) * graphics.getFont().size)).toInt()
            )
        }
        graphics.dispose()
        g2d.dispose()
    }

    /**
     * convert the meme image to a file, save it to the temp folder, and set it to auto delete later
     *
     * @return meme image converted to file
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun convertToFile(): File {
        val file = File("temp/meme$memeCount.jpg")
        ImageIO.write(meme, "jpg", file)
        memeCount++
        return file
    }

    companion object {
        /**
         * list of text lines loaded from the text file
         */
        val texts = ArrayList<String>()

        /**
         * number of memes created. helpful to separate meme files to prevent overwriting a meme being processed
         */
        private var memeCount = 0

        /**
         * last time the meme generator was used in milliseconds
         */
        private var prevTime = 0L

        /**
         * generate and send a meme
         *
         * @param command command entered by user
         */
        fun generate(command: Message) {
            val m = Meme(command)
            m.generate()
        }
    }
}