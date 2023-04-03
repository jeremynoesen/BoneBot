package xyz.jeremynoesen.bonebot.modules.commands

import net.coobird.thumbnailator.Thumbnails
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.utils.FileUpload
import org.apache.commons.text.WordUtils
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.awt.*
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import java.net.URLConnection
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Module to generate memes from various images and text sources
 *
 * @author Jeremy Noesen
 */
class Memes

/**
 * Constructs a new meme generator with the original message for get command arguments from
 *
 * @param command Command containing meme generator arguments
 */
constructor(private val command: Message) {

    /**
     * Meme text
     */
    private var text: String? = null

    /**
     * Background image
     */
    private var image: BufferedImage? = null

    /**
     * Generated meme image
     */
    private var meme: BufferedImage? = null

    /**
     * Generate and send a meme
     */
    fun generate() {
        if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
            try {
                readTextAndImage()
                if (image != null && text != null) {
                    text = text!!
                            .replace("\$PING\$", command.member!!.asMention)
                            .replace("\$NAME\$", command.member!!.effectiveName)
                            .replace("\$BOT\$", command.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                            .replace("\\n", "\n")
                    try {
                        text = text!!.replace("\$GUILD\$", command.guild.name)
                    } catch (e: IllegalStateException) {
                    }
                    processImage()
                    val embedBuilder = EmbedBuilder()
                    var title = Messages.memeTitle
                            .replace("\$NAME\$", command.member!!.effectiveName)
                            .replace("\$BOT\$", command.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                            .replace("\\n", "\n")
                            .replace("  ", " ")
                            .trim()
                    try {
                        title = title.replace("\$GUILD\$", command.guild.name)
                    } catch (e: IllegalStateException) {
                    }
                    if (title.contains(command.member!!.effectiveName)) {
                        embedBuilder.setAuthor(title, null, command.member!!.effectiveAvatarUrl)
                    } else if (title.contains(command.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)) {
                        embedBuilder.setAuthor(title, null, BoneBot.JDA!!.selfUser.effectiveAvatarUrl)
                    } else if (title.contains(command.guild.name)) {
                        embedBuilder.setAuthor(title, null, command.guild.iconUrl)
                    } else {
                        embedBuilder.setAuthor(title, null)
                    }
                    embedBuilder.setColor(Config.embedColor)
                    embedBuilder.setImage("attachment://meme.png")
                    val baos = ByteArrayOutputStream()
                    ImageIO.write(meme, "png", baos)
                    val bytes = baos.toByteArray()
                    command.channel.sendMessageEmbeds(embedBuilder.build())
                            .addFiles(FileUpload.fromData(bytes, "meme.png")).queue()
                    prevTime = System.currentTimeMillis()
                } else {
                    Messages.sendMessage(Messages.memeInputMissing, command)
                }
            } catch (exception: Exception) {
                Messages.sendMessage(Messages.error, command)
                exception.printStackTrace()
            }
        } else {
            val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
            Messages.sendMessage(Messages.memeCooldown.replace("\$TIME\$", (remaining + 1).toString()), command)
        }
    }

    /**
     * Read an image from URL
     *
     * @param url Image URL
     */
    private fun getImageFromURL(url: String): BufferedImage {
        val conn: URLConnection = URL(url).openConnection()
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
        conn.getInputStream().use { stream -> return Thumbnails.of(stream).scale(1.0).asBufferedImage() }
    }

    /**
     * Read an image from a discord message, user mention, or a random to use to generate a meme, as well as grab the
     * text from the message or use a random to make a meme
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readTextAndImage() {
        var input = command.contentDisplay.substring(
                Commands.prefix.length + Messages.memeCommand.length,
                command.contentDisplay.length
        )
        var altInput = ""
        var imageInput = false
        if (command.referencedMessage != null) altInput = command.referencedMessage!!.contentDisplay
        if (command.attachments.size > 0 && command.attachments[0].isImage) {
            image = getImageFromURL(command.attachments[0].url)
            imageInput = true
        } else if (command.embeds.size > 0 && command.embeds[0].image != null) {
            image = getImageFromURL(command.embeds[0].image!!.url!!)
            imageInput = true
        } else if (command.contentDisplay.contains("http://") ||
                command.contentDisplay.contains("https://")) {
            for (word in input.split(" ", "\n")) {
                try {
                    image = getImageFromURL(word.replace("<", "").replace(">", ""))
                    imageInput = true
                    break
                } catch (e: java.lang.Exception) {
                }
            }
        } else if (command.mentions.members.size > 0 &&
                (input.split(command.mentions.members[command.mentions.members.size - 1].effectiveName).size > 1 ||
                        input.split(command.mentions.members[command.mentions.members.size - 1].user.name).size > 1)
        ) {
            image =
                    getImageFromURL(command.mentions.members[command.mentions.members.size - 1]
                            .effectiveAvatarUrl + "?size=4096")
            imageInput = true
        } else if (command.referencedMessage != null) {
            val reply = command.referencedMessage!!
            if (reply.attachments.size > 0 && reply.attachments[0].isImage) {
                image = getImageFromURL(reply.attachments[0].url)
                imageInput = true
            } else if (reply.embeds.size > 0 && reply.embeds[0].image != null) {
                image = getImageFromURL(reply.embeds[0].image!!.url!!)
                imageInput = true
            } else if (reply.contentDisplay.contains("http://") ||
                    reply.contentDisplay.contains("https://")) {
                for (word in reply.contentDisplay.split(" ", "\n")) {
                    try {
                        image = getImageFromURL(word.replace("<", "")
                                .replace(">", ""))
                        imageInput = true
                        break
                    } catch (e: java.lang.Exception) {
                    }
                }
            } else if (reply.mentions.members.size > 0 &&
                    (altInput.split(reply.mentions.members[reply.mentions.members.size - 1].effectiveName).size > 1 ||
                            altInput.split(reply.mentions.members[reply.mentions.members.size - 1].user.name).size > 1)
            ) {
                image =
                        getImageFromURL(reply.mentions.members[reply.mentions.members.size - 1]
                                .effectiveAvatarUrl + "?size=4096")
                imageInput = true
            }
            if (image != null) {
                altInput = ""
            } else {
                altInput = cleanInput(altInput)
            }
        }
        if (!imageInput) {
            getRandomImage(File("resources/memeimages"))
        }
        input = cleanInput(input)
        if (input.trim().isNotEmpty()) {
            text = input
        } else if (altInput.trim().isNotEmpty() && command.referencedMessage != null) {
            text = altInput
        } else if (texts.isNotEmpty()) {
            text = texts[Random().nextInt(texts.size)]
        }
    }

    /**
     * Get random image to use for meme recursively
     *
     * @param dir Directory to look in for images
     */
    private fun getRandomImage(dir: File) {
        val r = Random()
        if (dir.listFiles()!!.isNotEmpty()) {
            var rand = r.nextInt(dir.listFiles()!!.size)
            val prev = HashSet<Int>()
            while (dir.listFiles()!![rand].isHidden) {
                rand = r.nextInt(dir.listFiles()!!.size)
                if (prev.contains(rand)) continue
                prev.add(rand)
                if (prev.size == dir.listFiles()!!.size) {
                    return
                }
            }
            if (dir.listFiles()!![rand].isDirectory) {
                getRandomImage(dir.listFiles()!![rand])
                return
            }
            image = Thumbnails.of(dir.listFiles()!![rand].inputStream()).scale(1.0).asBufferedImage()
        }
    }

    /**
     * Remove user pings and URLs from the text input
     *
     * @param input Input text
     * @return Output text with pings and URLs removed
     */
    private fun cleanInput(input: String): String {
        var output = input
        for (word in output.split(" ", "\n")) {
            if (word.contains("http://") || word.contains("https://")) {
                output = input.replace(word, "")
                        .replace("  ", " ").replace("<", "")
                        .replace(">", "")
            }
        }
        for (i in command.mentions.members.indices) output =
                input.replace("@${command.mentions.members[i].effectiveName}", "")
                        .replace("@${command.mentions.members[i].user.name}", "")
                        .replace("  ", " ")
        if (output.startsWith(Commands.prefix + Messages.memeCommand)) {
            output = output.substring(Commands.prefix.length + Messages.memeCommand.length, output.length)
        }
        return output
    }

    /**
     * Generate the meme image using the input text and image
     */
    @Throws(IOException::class, FontFormatException::class)
    private fun processImage() {
        var width = image!!.width
        var height = image!!.height
        if (size != 0) {
            if (width > height) {
                height = (size * (height / width.toDouble())).toInt()
                width = size
            } else {
                width = (size * (width / height.toDouble())).toInt()
                height = size
            }
        }
        meme = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = meme!!.graphics as Graphics2D
        val font =
                Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/Impact-Noto-Emoji.ttf"))
                        .deriveFont(((height + width) / 20.0f) * fontScale)
        g2d.font = font
        val metrics = g2d.getFontMetrics(font)
        g2d.drawImage(image, 0, 0, width, height, null)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        val sections = text!!.split("\n").toTypedArray()
        val topText = ArrayList<String>()
        val bottomText = ArrayList<String>()
        sections[0] = sections[0].trim()
        val topWrapLength =
                floor(sections[0].length /
                        (metrics.stringWidth(sections[0]) / (width.toFloat() - (width / 6.4)))).toInt()
        topText.addAll(
                listOf(
                        *WordUtils.wrap(sections[0], topWrapLength, "\n\n", true)
                                .split("\n\n").toTypedArray()
                )
        )
        if (sections.size > 1) {
            sections[1] = sections[1].trim()
            val bottomWrapLength =
                    floor(sections[1].length /
                            (metrics.stringWidth(sections[1]) / (width.toFloat() - (width / 6.4)))).toInt()
            bottomText.addAll(
                    listOf(
                            *WordUtils.wrap(sections[1], bottomWrapLength, "\n\n", true)
                                    .split("\n\n").toTypedArray()
                    )
            )
        }
        val outlineThickness = ceil(((height + width) / 450f) * fontScale)
        for (i in topText.indices) {
            val line = topText[i].trim { it <= ' ' }.uppercase()
            if (line.isEmpty()) break
            val shape = TextLayout(line, font, g2d.fontRenderContext).getOutline(null)
            g2d.stroke = BasicStroke(outlineThickness)
            val x = ((width - metrics.stringWidth(line)) / 2f).toInt()
            val y = (i + 1) * g2d.font.size
            val rect = shape.bounds
            rect.width += outlineThickness.toInt() + 1
            rect.height += outlineThickness.toInt()
            rect.x -= ((outlineThickness + 1) / 2.0).toInt()
            rect.y -= (outlineThickness / 2.0).toInt()
            g2d.translate(x, y)
            g2d.clip = rect
            g2d.color = Color.BLACK
            g2d.draw(shape)
            g2d.color = Color.WHITE
            g2d.fill(shape)
            g2d.translate(-x, -y)
        }
        for (i in bottomText.indices) {
            val line = bottomText[i].trim { it <= ' ' }.uppercase()
            if (line.isEmpty()) break
            val shape = TextLayout(line, font, g2d.fontRenderContext).getOutline(null)
            g2d.stroke = BasicStroke(outlineThickness)
            val x = ((width - metrics.stringWidth(line)) / 2f).toInt()
            val y = (height - (bottomText.size - i - 0.75) * g2d.font.size).toInt()
            val rect = shape.bounds
            rect.width += outlineThickness.toInt() + 1
            rect.height += outlineThickness.toInt()
            rect.x -= ((outlineThickness + 1) / 2.0).toInt()
            rect.y -= (outlineThickness / 2.0).toInt()
            g2d.translate(x, y)
            g2d.clip = rect
            g2d.color = Color.BLACK
            g2d.draw(shape)
            g2d.color = Color.WHITE
            g2d.fill(shape)
            g2d.translate(-x, -y)
        }
        g2d.dispose()
    }

    companion object {
        /**
         * List of text lines loaded from the text file
         */
        val texts = ArrayList<String>()

        /**
         * Cooldown for meme generator in seconds
         */
        var cooldown = 5

        /**
         * Whether this module is enabled or not
         */
        var enabled = true

        /**
         * Maximum size of a single dimension of generated memes in pixels
         */
        var size = 1200

        /**
         * Scale of text font on image
         */
        var fontScale = 1.0f

        /**
         * Last time the meme generator was used in milliseconds
         */
        private var prevTime = 0L
    }
}