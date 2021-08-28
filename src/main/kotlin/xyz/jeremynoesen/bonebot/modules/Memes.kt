package xyz.jeremynoesen.bonebot.modules

import net.coobird.thumbnailator.Thumbnails
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import org.apache.commons.text.WordUtils
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.awt.*
import java.awt.font.TextLayout
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.floor


/**
 * meme generator module
 *
 * @author Jeremy Noesen
 */
class Memes
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
                readTextAndImage()
                if (image != null && text != null) {
                    processImage()
                    val file = convertToFile()
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.setAuthor(
                        Messages.memeTitle.replace("\$USER\$", command.author.name),
                        null,
                        command.author.avatarUrl
                    )
                    embedBuilder.setColor(Config.embedColor)
                    embedBuilder.setImage("attachment://meme.png")
                    command.channel.sendMessage(embedBuilder.build()).addFile(file, "meme.png").queue()
                    prevTime = System.currentTimeMillis()
                } else {
                    command.channel.sendMessage(Messages.memeInputMissing).queue()
                }
            } catch (exception: Exception) {
                command.channel.sendMessage(Messages.error).queue()
                exception.printStackTrace()
            }
        } else {
            val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
            command.channel.sendMessage(Messages.memeCooldown.replace("\$TIME\$", remaining.toString())).queue()
        }
    }

    /**
     * read an image from url
     *
     * @param url string url
     */
    private fun getImageFromURL(url: String): BufferedImage {
        val conn: URLConnection = URL(url).openConnection()
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
        conn.getInputStream().use { stream -> return Thumbnails.of(stream).scale(1.0).asBufferedImage() }
    }

    /**
     * read an image from a discord message, user mention, or a random to use to generate a meme, as well as grab the
     * text from the message or use a random to make a meme
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readTextAndImage() {
        var input = command.contentDisplay.substring(
            Commands.commandPrefix.length + Messages.memeCommand.length,
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

        } else if (command.contentRaw.contains("http://") || command.contentRaw.contains("https://")) {
            for (word in input.split(" ", "\n", " // ")) {
                try {
                    image = getImageFromURL(word)
                    break
                } catch (e: java.lang.Exception) {
                }
            }
            imageInput = true

        } else if (command.mentionedUsers.size > 0 &&
            input.split(command.mentionedUsers[command.mentionedUsers.size - 1].name).size > 1
        ) {
            image =
                getImageFromURL(command.mentionedUsers[command.mentionedUsers.size - 1].effectiveAvatarUrl + "?size=4096")
            imageInput = true

        } else if (command.referencedMessage != null) {
            val reply = command.referencedMessage!!

            if (reply.attachments.size > 0 && reply.attachments[0].isImage) {
                image = getImageFromURL(reply.attachments[0].url)
                imageInput = true

            } else if (reply.embeds.size > 0 && reply.embeds[0].image != null) {
                image = getImageFromURL(reply.embeds[0].image!!.url!!)
                imageInput = true

            } else if (reply.contentRaw.contains("http://") || reply.contentRaw.contains("https://")) {
                for (word in reply.contentRaw.split(" ", "\n", " // ")) {
                    try {
                        image = getImageFromURL(word)
                        break
                    } catch (e: java.lang.Exception) {
                    }
                }
                imageInput = true

            } else if (reply.mentionedUsers.size > 0 &&
                altInput.split(reply.mentionedUsers[reply.mentionedUsers.size - 1].name).size > 1
            ) {
                image =
                    getImageFromURL(reply.mentionedUsers[reply.mentionedUsers.size - 1].effectiveAvatarUrl + "?size=4096")
                imageInput = true
            }

            if (image != null) {
                altInput = ""
            } else {
                for (word in altInput.split(" ", "\n", " // ")) {
                    if (word.startsWith("http://") || word.startsWith("https://")) {
                        altInput = altInput.replace(word, "").replace("   ", " ")
                            .replace("  ", " ")
                    }
                }

                for (i in reply.mentionedUsers.indices)
                    altInput = altInput.replace("@${reply.mentionedUsers[i].name}", "")
                        .replace("   ", " ").replace("  ", " ")
            }
        }

        if (!imageInput) {
            val r = Random()
            val dir = File("resources/memeimages")
            if (dir.listFiles()!!.isNotEmpty()) {
                var rand = r.nextInt(dir.listFiles()!!.size)
                val prev = HashSet<Int>()
                while (dir.listFiles()!![rand].isHidden || dir.listFiles()!![rand].isDirectory) {
                    rand = r.nextInt(dir.listFiles()!!.size)
                    if (prev.contains(rand)) continue
                    prev.add(rand)
                    if (prev.size == dir.listFiles()!!.size) {
                        return
                    }
                }
                image = Thumbnails.of(dir.listFiles()!![rand].inputStream()).scale(1.0).asBufferedImage()
            }
        }

        for (word in input.split(" ", "\n", " // ")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                input = input.replace(word, "").replace("   ", " ")
                    .replace("  ", " ")
            }
        }

        for (i in command.mentionedUsers.indices) input =
            input.replace("@${command.mentionedUsers[i].name}", "")
                .replace("   ", " ").replace("  ", " ")

        if (input.trim().isNotEmpty()) {
            text = input
        } else if (altInput.trim().isNotEmpty() && command.referencedMessage != null) {
            text = altInput
        } else if (texts.isNotEmpty()) {
            text = texts[Random().nextInt(texts.size)]
        }
    }

    /**
     * generate a meme using the input text and image
     */
    @Throws(IOException::class, FontFormatException::class)
    private fun processImage() {
        val width = if (size == 0) image!!.width else size
        val height = if (size == 0) image!!.height else (width * (image!!.height / image!!.width.toDouble())).toInt()
        meme = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = meme!!.graphics as Graphics2D
        val font =
            Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/Impact.ttf"))
                .deriveFont((height + width) / 20.0f)
        g2d.font = font
        val metrics = g2d.getFontMetrics(font)
        g2d.drawImage(image, 0, 0, width, height, null)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        val sections = text!!.split("\n", " // ").toTypedArray()
        val topText = ArrayList<String>()
        val bottomText = ArrayList<String>()

        val topWrapLength =
            floor(sections[0].length / (metrics.stringWidth(sections[0]) / (width.toFloat() - (width / 6.4)))).toInt()
        topText.addAll(
            listOf(
                *WordUtils.wrap(sections[0], topWrapLength, "\n\n", true).split("\n\n").toTypedArray()
            )
        )
        if (sections.size > 1) {
            val bottomWrapLength =
                floor(sections[1].length / (metrics.stringWidth(sections[1]) / (width.toFloat() - (width / 6.4)))).toInt()
            bottomText.addAll(
                listOf(
                    *WordUtils.wrap(sections[1], bottomWrapLength, "\n\n", true).split("\n\n").toTypedArray()
                )
            )
        }

        val outlineThickness = ceil((height + width) / 450f)

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
            val y = (height - (bottomText.size - i - 0.8) * g2d.font.size).toInt()
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
         * width of generated memes
         */
        var size = 1200

        /**
         * last time the meme generator was used in milliseconds
         */
        private var prevTime = 0L
    }
}