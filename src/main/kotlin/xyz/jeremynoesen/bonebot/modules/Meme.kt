package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import org.apache.commons.text.WordUtils
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Logger
import java.awt.*
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.floor


/**
 * meme generator
 *
 * @author Jeremy Noesen
 */
class Meme
/**
 * constructs a new meme generator class with the original message for get command arguments from
 *
 * @param command command containing meme arguments
 */
constructor(private val command: Message) {

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
    fun generate() {
        if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
            try {
                command.channel.sendTyping().queue()
                readTextAndImage()
                if (image != null && text != null) {
                    processImage()
                    val file = convertToFile()
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.setAuthor(command.author.name + " generated a meme:", null, command.author.avatarUrl)
                    embedBuilder.setColor(Config.embedColor)
                    embedBuilder.setImage("attachment://meme.png")
                    command.channel.sendMessage(embedBuilder.build()).addFile(file, "meme.png").queue()
                    prevTime = System.currentTimeMillis()
                } else {
                    command.channel.sendMessage("Please provide the missing text or image!").queue()
                }
            } catch (exception: Exception) {
                Logger.log(exception)
            }
        } else {
            val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
            command.channel.sendMessage("Another meme can be generated in **$remaining** seconds.").queue()
        }
    }

    /**
     * read an image from a url
     *
     * @param url string url
     */
    private fun getImageFromURL(url: String): BufferedImage {
        val conn: URLConnection = URL(url).openConnection()
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
        conn.getInputStream().use { stream -> return ImageIO.read(stream) }
    }

    /**
     * read an image from a discord message, user mention, or a random to use to generate a meme, as well as grab the
     * text from the message or use a random to make a meme
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readTextAndImage() {
        var input = command.contentRaw.substring(Command.commandPrefix.length + 4, command.contentRaw.length)

        if (command.referencedMessage != null) {
            if (command.referencedMessage!!.attachments.size > 0 && command.referencedMessage!!.attachments[0].isImage) {
                image = getImageFromURL(command.referencedMessage!!.attachments[0].url)
            } else if (command.referencedMessage!!.embeds.size > 0 && command.referencedMessage!!.embeds[0].image != null) {
                image = getImageFromURL(command.referencedMessage!!.embeds[0].image!!.url!!)
            } else {
                for (word in command.referencedMessage!!.contentRaw.split(" ", "\n", " // ")) {
                    try {
                        image = getImageFromURL(word)
                        break
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
        } else {

            if (command.attachments.size > 0 && command.attachments[0].isImage) {
                image = getImageFromURL(command.attachments[0].url)
            } else if (command.mentionedUsers.size > 0) {
                image = getImageFromURL(command.mentionedUsers[0].effectiveAvatarUrl + "?size=1024")
                for (i in command.mentionedUsers.indices) input = input.replace(command.mentionedUsers[i].asMention, "")
                    .replace("<@!" + command.mentionedUsers[i].idLong + ">", "")
                    .replace("  ", " ")
            } else {

                for (word in input.split(" ", "\n", " // ")) {
                    try {
                        image = getImageFromURL(word)
                        input = input.replace(word, "").replace("  ", " ")
                        break
                    } catch (e: java.lang.Exception) {
                    }
                }

                if (image == null) {
                    val r = Random()
                    val dir = File("resources/images")
                    if (dir.listFiles()!!.isNotEmpty()) {
                        var rand = r.nextInt(dir.listFiles()!!.size)
                        while (dir.listFiles()!![rand].isHidden) {
                            rand = r.nextInt(dir.listFiles()!!.size)
                        }
                        image = ImageIO.read(dir.listFiles()!![rand])
                    }
                }
            }
        }

        if (input.trim().isNotEmpty()) {
            text = input
        } else if (texts.isNotEmpty()) {
            val r = Random()
            text = texts[r.nextInt(texts.size)]
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
        meme = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = meme!!.graphics
        val g2d = graphics as Graphics2D
        val font =
            Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/Impact.ttf"))
                .deriveFont((height + width) / 20.0f)
        g2d.font = font
        val metrics = graphics.getFontMetrics(font)
        g2d.drawImage(image, 0, 0, width, height, null)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        val sections = text!!.split("\n", " // ").toTypedArray()
        val topText = ArrayList<String>()
        val bottomText = ArrayList<String>()

        val topWrapLength = floor(sections[0].length / (metrics.stringWidth(sections[0]) / (width.toFloat() - 160))).toInt()
        topText.addAll(
            listOf(
                *WordUtils.wrap(sections[0], topWrapLength, "\n\n", true).split("\n\n").toTypedArray()
            )
        )
        if (sections.size > 1) {
            val bottomWrapLength = floor(sections[1].length / (metrics.stringWidth(sections[1]) / (width.toFloat() - 160))).toInt()
            bottomText.addAll(
                listOf(
                    *WordUtils.wrap(sections[1], bottomWrapLength, "\n\n", true).split("\n\n").toTypedArray()
                )
            )
        }

        for (i in topText.indices) {
            val line = topText[i].trim { it <= ' ' }.toUpperCase()
            if (line.isEmpty()) continue
            g2d.color = Color.WHITE
            g2d.drawString(
                line,
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                ((i + 0.9) * g2d.font.size).toInt()
            )
            val shape = TextLayout(line, font, g2d.fontRenderContext).getOutline(null)
            g2d.stroke = BasicStroke(3f)
            g2d.translate(
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                ((i + 0.9) * g2d.font.size).toInt()
            )
            g2d.color = Color.BLACK
            g2d.draw(shape)
            g2d.translate(
                (-((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0)).toInt(),
                (-((i + 0.9) * g2d.font.size)).toInt()
            )
        }

        for (i in bottomText.indices) {
            val line = bottomText[i].trim { it <= ' ' }.toUpperCase()
            if (line.isEmpty()) continue
            g2d.color = Color.WHITE
            g2d.drawString(
                line,
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                (meme!!.getHeight(null) - (bottomText.size - i - 0.9) * g2d.font.size).toInt()
            )
            val shape = TextLayout(line, font, g2d.fontRenderContext).getOutline(null)
            g2d.stroke = BasicStroke(3f)
            g2d.translate(
                ((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0).toInt(),
                (meme!!.getHeight(null) - (bottomText.size - i - 0.9) * g2d.font.size).toInt()
            )
            g2d.color = Color.BLACK
            g2d.draw(shape)
            g2d.translate(
                (-((meme!!.getWidth(null) - metrics.stringWidth(line)) / 2.0)).toInt(),
                (-(meme!!.getHeight(null) - (bottomText.size - i - 0.9) * g2d.font.size)).toInt()
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
        File("temp").mkdir()
        val file = File("temp/meme$memeCount.png")
        ImageIO.write(meme, "png", file)
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
         * cooldown for meme generator, in seconds
         */
        var cooldown = 5

        /**
         * whether this module is enabled or not
         */
        var enabled = true

        /**
         * last time the meme generator was used in milliseconds
         */
        private var prevTime = 0L
    }
}