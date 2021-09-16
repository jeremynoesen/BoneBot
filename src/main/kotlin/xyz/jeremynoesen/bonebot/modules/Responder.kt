package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.io.File

/**
 * responder to respond to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Responder {

    /**
     * list of phrases loaded from the responses file
     */
    val responses = LinkedHashMap<String, String>()

    /**
     * cooldown for responder, in seconds
     */
    var cooldown = 180

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * speed at which the bot types each letter
     */
    var typingSpeed = 100L

    /**
     * last time the responder sent a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * respond to a message if a trigger phrase is said
     *
     * @param message message to check and respond to
     */
    fun respond(message: Message) {
        try {
            val msg = message.contentDisplay
            for (trigger in responses.keys) {
                val editedTrigger = trigger.replace("\$NAME\$", message.author.name)
                    .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                    .replace("\$GUILD\$", message.guild.name)
                if ((msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase()))
                    && (System.currentTimeMillis() - prevTime) >= cooldown * 1000
                ) {
                    prevTime = System.currentTimeMillis()

                    if (typingSpeed > 0) message.channel.sendTyping().queue()

                    var toSend = responses[trigger]!!.replace("\$USER\$", message.author.asMention)
                        .replace("\$NAME\$", message.author.name)
                        .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                        .replace("\$GUILD\$", message.guild.name)
                        .replace("\\n", "\n")

                    var file: File? = null

                    if (toSend.contains("\$FILE\$")) {
                        val path = toSend.split("\$FILE\$")[1].trim()
                        toSend = toSend.replace(
                            toSend.substring(
                                toSend.indexOf("\$FILE\$"),
                                toSend.lastIndexOf("\$FILE\$") + 6
                            ), ""
                        )
                            .replace("  ", " ").trim()
                        file = File(path)
                        if (!file.exists() || file.isDirectory || file.isHidden) {
                            file = null
                        }
                    }

                    if (toSend.contains("\$REPLY\$")) {
                        toSend = toSend.replace("\$REPLY\$", "").replace("   ", " ")
                            .replace("  ", " ")
                        if (file != null) {
                            if (toSend.isNotEmpty())
                                message.channel.sendMessage(toSend).addFile(file).reference(message).queue()
                            else
                                message.channel.sendFile(file).reference(message).queue()
                        } else {
                            if (toSend.isNotEmpty())
                                message.channel.sendMessage(toSend).reference(message).queue()
                        }
                    } else {
                        if (file != null) {
                            if (toSend.isNotEmpty())
                                message.channel.sendMessage(toSend).addFile(file).queue()
                            else
                                message.channel.sendFile(file).queue()
                        } else {
                            if (toSend.isNotEmpty())
                                message.channel.sendMessage(toSend).queue()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}