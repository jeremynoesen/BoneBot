package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.utils.FileUpload
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.io.File
import java.lang.IllegalStateException
import kotlin.random.Random

/**
 * Module to respond to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Responder {

    /**
     * List of phrases loaded from the responses file
     */
    val responses = LinkedHashMap<String, String>()

    /**
     * Cooldown for responder in seconds
     */
    var cooldown = 180

    /**
     * Whether this module is enabled or not
     */
    var enabled = true

    /**
     * Delay before starting to reply in milliseconds
     */
    var delay = 1000L

    /**
     * Speed at which the bot types each letter
     */
    var typingSpeed = 100L

    /**
     * Last time the responder sent a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * Respond to a message if a trigger phrase is said
     *
     * @param message Message to check and respond to
     */
    fun respond(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                val msg = message.contentDisplay
                for (trigger in responses.keys) {
                    var editedTrigger = trigger.replace("\$PING\$", message.member!!.asMention)
                            .replace("\$NAME\$", message.member!!.effectiveName)
                            .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                            .replace("\\n", "\n")
                            .replace("  ", " ")
                            .trim()
                    try {
                        editedTrigger = editedTrigger.replace("\$GUILD\$", message.guild.name)
                    } catch (_: IllegalStateException) {
                    }
                    if (msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase())) {
                        prevTime = System.currentTimeMillis()
                        val randomResponses = responses[trigger]!!.split("\$||\$")
                        val selectedResponse = randomResponses[Random.nextInt(randomResponses.size)]
                        for (responseMessage in selectedResponse.split("\$&&\$")) {
                            var toSend = responseMessage.replace("\$PING\$", message.member!!.asMention)
                                    .replace("\$NAME\$", message.member!!.effectiveName)
                                    .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!
                                            .effectiveName)
                                    .replace("\\n", "\n")
                                    .replace("  ", " ")
                                    .trim()
                            try {
                                toSend = toSend.replace("\$GUILD\$", message.guild.name)
                            } catch (_: IllegalStateException) {
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
                                Thread.sleep(delay)
                                if (typingSpeed * toSend.length > 0) {
                                    var typingTime = 0L
                                    for (i in toSend.indices) {
                                        if (typingTime >= 5000L) typingTime = 0L
                                        if (typingTime == 0L) message.channel.sendTyping().queue()
                                        typingTime += typingSpeed
                                        Thread.sleep(typingSpeed);
                                    }
                                }
                                if (file != null) {
                                    if (toSend.isNotEmpty())
                                        message.channel.sendMessage(toSend).addFiles(FileUpload.fromData(file))
                                                .setMessageReference(message).queue()
                                    else
                                        message.channel.sendFiles(FileUpload.fromData(file))
                                                .setMessageReference(message).queue()
                                } else {
                                    if (toSend.isNotEmpty())
                                        message.channel.sendMessage(toSend).setMessageReference(message).queue()
                                }
                            } else {
                                Thread.sleep(delay)
                                if (typingSpeed * toSend.length > 0) {
                                    var typingTime = 0L
                                    for (i in toSend.indices) {
                                        if (typingTime >= 5000L) typingTime = 0L
                                        if (typingTime == 0L) message.channel.sendTyping().queue()
                                        typingTime += typingSpeed
                                        Thread.sleep(typingSpeed);
                                    }
                                }
                                if (file != null) {
                                    if (toSend.isNotEmpty())
                                        message.channel.sendMessage(toSend).addFiles(FileUpload.fromData(file)).queue()
                                    else
                                        message.channel.sendFiles(FileUpload.fromData(file)).queue()
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