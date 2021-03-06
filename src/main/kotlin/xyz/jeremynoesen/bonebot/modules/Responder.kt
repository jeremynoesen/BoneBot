package xyz.jeremynoesen.bonebot.modules

import xyz.jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * responder to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Responder {

    /**
     * list of phrases loaded from the responses file
     */
    val responses = HashMap<String, String>()

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
            val msg = message.contentRaw.toLowerCase()
            for (trigger in responses.keys) {
                if (msg.contains(Regex(trigger)) && (System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    if (typingSpeed > 0) message.channel.sendTyping().queue()
                    val toSend = responses[trigger]!!.replace("\$USER$", message.author.asMention)
                    message.channel.sendMessage(toSend).queueAfter(toSend.length * typingSpeed, TimeUnit.MILLISECONDS)
                    break
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}