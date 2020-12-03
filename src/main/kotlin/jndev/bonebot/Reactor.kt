package jndev.bonebot

import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * responder to words and phrases in a message
 *
 * @author JNDev (Jeremaster101)
 */
object Reactor {
    /**
     * list of phrases loaded from the responses file
     */
    val reactions = ArrayList<String>()

    /**
     * react to a message if a trigger phrase is said
     *
     * @param message message to check and react to
     */
    fun react(message: Message) {
        val msg = message.contentRaw.toLowerCase()
        for (phrase in reactions) {
            val triggerAndEmotes = phrase.split(" // ").toTypedArray()
            val triggers = triggerAndEmotes[0].split(" / ").toTypedArray()
            var count = 0
            for (trigger in triggers) {
                if (msg.contains(trigger.toLowerCase())) count++
            }
            if (count == triggers.size) {
                for (i in 1 until triggerAndEmotes.size) message.addReaction(triggerAndEmotes[i]).queue()
            }
        }
    }
}