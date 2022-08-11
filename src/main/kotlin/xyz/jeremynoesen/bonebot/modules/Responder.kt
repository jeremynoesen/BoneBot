package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.io.File
import java.lang.IllegalStateException
import kotlin.random.Random

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
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                val msg = message.contentDisplay
                for (trigger in responses.keys) {
                    var editedTrigger = trigger.replace("\$NAME\$", message.author.name)
                            .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                            .replace("\\n", "\n")
                    try {
                        editedTrigger = editedTrigger.replace("\$GUILD\$", message.guild.name)
                    } catch (e: IllegalStateException) {
                    }
                    if (msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase())) {
                        prevTime = System.currentTimeMillis()


                        val randomResponses = responses[trigger]!!.split(" || ")
                        val selectedResponse = randomResponses[Random.nextInt(randomResponses.size)]

                        for (responseMessage in selectedResponse.split(" && ")) {

                            var toSend = responseMessage.replace("\$USER\$", message.author.asMention)
                                    .replace("\$NAME\$", message.author.name)
                                    .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                                    .replace("\\n", "\n")
                            try {
                                toSend = toSend.replace("\$GUILD\$", message.guild.name)
                            } catch (e: IllegalStateException) {
                            }

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
                                toSend = toSend.replace("\$REPLY\$", "")
                                        .replace("  ", " ")

                                if (typingSpeed > 0) {
                                    var typingTime = 0L
                                    for (i in toSend.indices) {
                                        if (typingTime >= 9000L) typingTime = 0L
                                        if (typingTime == 0L) message.channel.sendTyping().queue()
                                        typingTime += typingSpeed
                                        Thread.sleep(typingSpeed);
                                    }
                                }

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

                                if (typingSpeed > 0) {
                                    var typingTime = 0L
                                    for (i in toSend.indices) {
                                        if (typingTime >= 9000L) typingTime = 0L
                                        if (typingTime == 0L) message.channel.sendTyping().queue()
                                        typingTime += typingSpeed
                                        Thread.sleep(typingSpeed);
                                    }
                                }

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
                }
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}