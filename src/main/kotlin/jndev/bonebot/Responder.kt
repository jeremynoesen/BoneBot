package jndev.bonebot

import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * responder to words and phrases in a message
 *
 * @author JNDev (Jeremaster101)
 */
object Responder {
    /**
     * list of phrases loaded from the responses file
     */
    val responses = ArrayList<String>()

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
        val msg = message.contentRaw.toLowerCase()
        for (phrase in responses) {
            val triggerAndPhrases = phrase.split(" // ").toTypedArray()
            val triggers = triggerAndPhrases[0].split(" / ").toTypedArray()
            var count = 0
            for (trigger in triggers) {
                if (msg.contains(trigger.toLowerCase())) count++
            }
            if (count == triggers.size && (System.currentTimeMillis() - prevTime) >= Config.responseCooldown * 1000) {
                prevTime = System.currentTimeMillis()
                for (i in 1 until triggerAndPhrases.size) message.channel.sendMessage(
                    triggerAndPhrases[1]
                        .replace("\$USER$", message.author.asMention)
                ).queue()
            }
        }
    }
}